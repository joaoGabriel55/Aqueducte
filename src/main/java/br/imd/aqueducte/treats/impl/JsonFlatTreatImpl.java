package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.treats.JsonFlatTreat;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.imd.aqueducte.utils.GeoJsonValidator.getGeoJson;

@SuppressWarnings("ALL")
@Log4j2
public class JsonFlatTreatImpl implements JsonFlatTreat {

    private String keyPath = "";

    @Override
    public Object getFlatJSON(Object dataForConversion) throws Exception {
        if ((dataForConversion instanceof Map)) {
            Map<String, Object> result = new LinkedHashMap<>();
            Map<String, Object> dataMap = (Map<String, Object>) dataForConversion;
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                Map<String, Object> jsonFlatAux = new LinkedHashMap<>();
                Map<String, Object> objectMap = new LinkedHashMap<>();
                objectMap.put(entry.getKey(), entry.getValue());
                result.putAll(convertToJsonFlat(jsonFlatAux, objectMap));
            }
            if (result.size() == 0)
                log.error("result is empty");
            log.info("result successfuly");
            return result;
        } else if (dataForConversion instanceof List) {
            List<Object> result = (List<Object>) ((List) dataForConversion)
                    .stream()
                    .map(elem -> {
                        Map<String, Object> jsonFlatAux = new LinkedHashMap<>();
                        return convertToJsonFlat(jsonFlatAux, elem);
                    }).collect(Collectors.toList());
            if (result.size() == 0)
                log.error("result is empty");
            return result;
        }
        log.error("result error");
        throw new Exception();
    }

    @Override
    public List<String> getKeysCollectionFromJSON(Map<String, Object> dataForConversion) {
        return getKeysArray(dataForConversion);
    }

    private List<String> getKeysArray(Map<String, Object> jsonData) {
        Map<String, Object> jsonDataTyped = jsonData;
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : jsonDataTyped.entrySet()) {
            if (entry.getValue() instanceof List)
                keyList.add(entry.getKey());
        }
        return keyList;
    }

    private Map<String, Object> convertToJsonFlat(
            Map<String, Object> jsonFlatAux,
            Object jsonData) {
        Map<String, Object> jsonDataTyped = (Map<String, Object>) jsonData;
        if (jsonDataTyped.get("data") instanceof List) {
            jsonDataTyped = (Map<String, Object>) jsonData;
        } else if (jsonDataTyped.get("data") instanceof Map) {
            jsonDataTyped = (Map<String, Object>) ((Map<String, Object>) jsonData).get("data");
        }
        for (Map.Entry<String, Object> entry : jsonDataTyped.entrySet()) {
            if ((entry.getValue() instanceof Map) && !(entry.getValue() instanceof List)) {
                boolean isGeoJson = false;
                try {
                    isGeoJson = getGeoJson(entry.getValue()) == null ? false : true;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!isGeoJson) {
                    if (keyPath != "")
                        keyPath += "_" + entry.getKey();
                    else
                        keyPath = entry.getKey();

                    convertToJsonFlat(jsonFlatAux, entry.getValue());
                } else {
                    if (keyPath != "") {
                        String keyFinal = keyPath + "_" + entry.getKey();
                        jsonFlatAux.put(keyFinal, entry.getValue());
                    } else {
                        jsonFlatAux.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                if (keyPath != "") {
                    String keyFinal = keyPath + "_" + entry.getKey();
                    jsonFlatAux.put(keyFinal, entry.getValue());
                } else {
                    jsonFlatAux.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (keyPath != "")
            keyPath = "";
        return jsonFlatAux;
    }
}

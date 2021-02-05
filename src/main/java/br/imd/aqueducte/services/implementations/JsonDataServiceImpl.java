package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.services.JsonDataService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.imd.aqueducte.utils.GeoJsonValidator.getGeoJson;

@SuppressWarnings("ALL")
@Service
@Log4j2
public class JsonDataServiceImpl implements JsonDataService {

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
                result.putAll(convertToJsonFlat(objectMap));
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
                        return convertToJsonFlat((Map<String, Object>) elem);
                    }).collect(Collectors.toList());
            if (result.size() == 0)
                log.error("result is empty");
            return result;
        }
        log.error("result error");
        throw new Exception();
    }

    private Map<String, Object> convertToJsonFlat(Map<String, Object> content) {
        var entries = content.entrySet();
        var result = content;
        try {
            while (hasAnyObject(entries)) {
                result = flatten(entries);
                entries = result.entrySet();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Map<String, Object> flatten(Set<Map.Entry<String, Object>> entries) throws Exception {
        var objectFlat = new LinkedHashMap<String, Object>();
        try {
            for (Map.Entry<String, Object> entry : entries) {
                var key = entry.getKey();
                var value = entry.getValue();

                boolean isGeoJson = false;
                if (value instanceof Map)
                    isGeoJson = getGeoJson(value) == null ? false : true;

                if (!isGeoJson && (value instanceof Map)) {
                    Map<String, Object> mapValue = (Map<String, Object>) value;
                    Map<String, Object> mapNew = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry2 : mapValue.entrySet()) {
                        mapNew.put(key + "_" + entry2.getKey(), entry2.getValue());
                    }
                    objectFlat.putAll(mapNew);
                } else {
                    objectFlat.put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new Exception(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return objectFlat;
    }

    private boolean hasAnyObject(Set<Map.Entry<String, Object>> entries) {
        boolean hasAnyObject = entries.stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .filter(entry -> {
                    boolean isGeoJson = false;
                    try {
                        isGeoJson = getGeoJson(entry.getValue()) == null ? false : true;
                    } catch (org.codehaus.jettison.json.JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return !isGeoJson;
                })
                .collect(Collectors.toList()).size() > 0;
        return hasAnyObject;
    }

    @Override
    public List<String> getCollectionKeysFromJSON(Map<String, Object> dataForConversion) {
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
}

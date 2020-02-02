package br.imd.aqueducte.treats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.imd.aqueducte.utils.FormatterUtils.checkIsGeoJson;

@SuppressWarnings("ALL")
public class JsonFlatConvertTreat {

    private String keyPath = "";


    public Object getJsonFlat(Object dataForConversion) {
        if (dataForConversion instanceof Map) {
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> dataMap = (Map<String, Object>) dataForConversion;
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                Map<String, Object> jsonFlatAux = new HashMap<>();
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put(entry.getKey(), entry.getValue());
                result = convertToJsonFlat(jsonFlatAux, objectMap);
            }
            return result;
        } else if (dataForConversion instanceof List) {
            List<Object> result = (List<Object>) ((List) dataForConversion)
                    .stream()
                    .map(elem -> {
                        Map<String, Object> jsonFlatAux = new HashMap<>();
                        return convertToJsonFlat(jsonFlatAux, elem);
                    }).collect(Collectors.toList());
            return result;
        }
        return null;
    }

    public List<String> getArrayKeysFromJsonData(Map<String, Object> dataForConversion) {
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
        for (Map.Entry<String, Object> entry : jsonDataTyped.entrySet()) {
            if ((entry.getValue() instanceof Map) && !(entry.getValue() instanceof List)) {
                boolean isGeoJson = checkIsGeoJson((Map<String, Object>) entry.getValue());
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

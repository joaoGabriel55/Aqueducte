package br.imd.aqueducte.treats;

import br.imd.aqueducte.utils.NGSILDUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonFlatConvertTreat {

    private NGSILDUtils ngsildUtils;

    public JsonFlatConvertTreat() {
        this.ngsildUtils = new NGSILDUtils();
    }

    public List<Object> getJsonFlatCollection(List<Object> dataForConversion) {
        List<Object> jsonDataFlat = dataForConversion
                .stream()
                .map(elem -> {
                    String keyPath = "";
                    Map<String, Object> jsonFlatAux = new HashMap<>();
                    return convertToJsonFlat(keyPath, jsonFlatAux, elem);
                })
                .collect(Collectors.toList());
        return jsonDataFlat;
    }

    private Map<String, Object> convertToJsonFlat(String keyPath,
                                                  Map<String, Object> jsonFlatAux,
                                                  Object jsonData) {
        Map<String, Object> jsonDataTyped = (Map<String, Object>) jsonData;
        for (Map.Entry<String, Object> entry : jsonDataTyped.entrySet()) {
            if ((entry.getValue() instanceof Map) && !(entry.getValue() instanceof List)) {
                boolean isGeoJson = this.ngsildUtils.checkIsGeoJson((Map<String, Object>) entry.getValue());
                if (!isGeoJson) {
                    if (keyPath != "")
                        keyPath += "_" + entry.getKey();
                    else
                        keyPath = entry.getKey();

                    convertToJsonFlat(keyPath, jsonFlatAux, entry.getValue());
                } else {
                    jsonFlatAux.put(entry.getKey(), entry.getValue());
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
        return jsonFlatAux;
    }
}

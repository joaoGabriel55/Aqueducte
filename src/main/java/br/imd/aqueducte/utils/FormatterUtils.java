package br.imd.aqueducte.utils;

import java.util.Map;

public class FormatterUtils {

    public static boolean checkIsGeoJson(Map<String, Object> jsonData) {
        boolean hasAnyGeoType = jsonData.get("type") != null &&
                (jsonData.get("type").equals("Polygon") ||
                        jsonData.get("type").equals("Point") ||
                        jsonData.get("type").equals("LineString") ||
                        jsonData.get("type").equals("MultiPoint") ||
                        jsonData.get("type").equals("MultiLineString") ||
                        jsonData.get("type").equals("MultiPolygon"));

        return jsonData.containsKey("type") && jsonData.containsKey("coordinates") && hasAnyGeoType;
    }

}

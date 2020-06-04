package br.imd.aqueducte.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeoJsonValidator {
    public static Map<String, Object> getGeoJson(Object jsonData) throws JSONException, IOException {
        if (jsonData instanceof Map) {
            Map<String, Object> jsonDataMap = (Map<String, Object>) jsonData;
            boolean hasAnyGeoType = jsonDataMap.get("type") != null &&
                    (jsonDataMap.get("type").equals("Polygon") ||
                            jsonDataMap.get("type").equals("Point") ||
                            jsonDataMap.get("type").equals("LineString") ||
                            jsonDataMap.get("type").equals("MultiPoint") ||
                            jsonDataMap.get("type").equals("MultiLineString") ||
                            jsonDataMap.get("type").equals("MultiPolygon"));
            if (jsonDataMap.containsKey("type") && jsonDataMap.containsKey("coordinates") && hasAnyGeoType)
                return jsonDataMap;
            return null;
        } else if (jsonData instanceof String) {
            Geometry geometry = geoJsonParser(jsonData.toString());
            if (geometry != null) {
                Map<String, Object> result = new ObjectMapper().readValue(jsonData.toString(), LinkedHashMap.class);
                return result;
            }
            return null;
        }
        return null;
    }

    private static JSONObject getJSON(String value) throws JSONException {
        return new JSONObject(value);
    }

    private static Geometry geoJsonParser(String value) throws JSONException, IOException {
        JSONObject json = getJSON(value);
        GeometryJSON ret = new GeometryJSON(12);
        StringReader reader = new StringReader(json.toString());
        return ret.read(reader);
    }
}

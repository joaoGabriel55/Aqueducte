package br.imd.aqueducte.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NGSILDUtils {

    public void initDefaultProperties(
            Map<String, Object> linkedHashMapNGSILD,
            List<String> contextList,
            String layerType,
            String uuid) {

        List<String> contextListDefault = new ArrayList<>();
        contextListDefault
                .add("https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");
        if (contextList != null) {
            for (String contextLink : contextList)
                contextListDefault.add(contextLink);
        }

        linkedHashMapNGSILD.put("@context", contextListDefault);
        linkedHashMapNGSILD.put("id", "urn:ngsi-ld:" + layerType + ":" + uuid);
        linkedHashMapNGSILD.put("type", layerType);
    }

    public boolean checkIsGeoJson(Map<String, Object> jsonData) {

        boolean hasAnyGeoType = jsonData.get("type") != null &&
                (jsonData.get("type").equals("Polygon") ||
                        jsonData.get("type").equals("Point") ||
                        jsonData.get("type").equals("LineString") ||
                        jsonData.get("type").equals("MultiPoint") ||
                        jsonData.get("type").equals("MultiLineString") ||
                        jsonData.get("type").equals("MultiPolygon"));

        if (jsonData.containsKey("type") && jsonData.containsKey("coordinates") && hasAnyGeoType) {
            return true;
        }
        return false;
    }
}

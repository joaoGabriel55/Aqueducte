package br.imd.aqueducte.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NGSILDUtils {
    public static List<String> contextList = Arrays.asList(
            "https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");

    public static void initDefaultProperties(Map<String, Object> linkedHashMapNGSILD, List<String> contextList,
                                             String layerType, String uuid) {
        linkedHashMapNGSILD.put("@context", contextList);
        linkedHashMapNGSILD.put("id", "urn:ngsi-ld:" + layerType + ":" + uuid);
        linkedHashMapNGSILD.put("type", layerType);
    }
}

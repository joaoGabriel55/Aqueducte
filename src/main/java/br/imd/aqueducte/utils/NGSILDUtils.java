package br.imd.aqueducte.utils;

import java.util.*;

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
}

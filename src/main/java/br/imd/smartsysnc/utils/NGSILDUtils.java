package br.imd.smartsysnc.utils;

import java.util.LinkedHashMap;
import java.util.List;

public class NGSILDUtils {
	public static void initDefaultProperties(LinkedHashMap<Object, Object> linkedHashMapNGSILD, List<String> contextList,
			String layerType, String uuid) {
		linkedHashMapNGSILD.put("@context", contextList);
		linkedHashMapNGSILD.put("id", "urn:ngsi-ld:" + layerType + ":" + uuid);
		linkedHashMapNGSILD.put("type", layerType);
	}
}

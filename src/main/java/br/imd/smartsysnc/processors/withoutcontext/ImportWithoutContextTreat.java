package br.imd.smartsysnc.processors.withoutcontext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import br.imd.smartsysnc.processors.NGSILDTreat;
import br.imd.smartsysnc.utils.NGSILDUtils;

public class ImportWithoutContextTreat implements NGSILDTreat {

	@SuppressWarnings("unchecked")
	public List<LinkedHashMap<Object, Object>> converterToEntityNGSILD(List<Object> data, String layerPath,
			Map<Object, Object> contextLink) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

		HashMap<Object, Object> value = new HashMap<>();

		// Property data provided from external API
		ArrayList<Object> dataListFromExternalAPI = (ArrayList<Object>) data;

		List<LinkedHashMap<Object, Object>> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Entry<Object, Object> propertiesContent = null;

		List<String> contextList = Arrays.asList(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");

		for (int i = 0; i < dataListFromExternalAPI.size(); i++) {
			LinkedHashMap<Object, Object> ldObj;
			ldObj = (LinkedHashMap<Object, Object>) dataListFromExternalAPI.get(i);
			UUID uuid = UUID.randomUUID();
			NGSILDUtils.initDefaultProperties(linkedHashMapNGSILD, contextList, layerPath, uuid.toString());

			HashMap<Object, HashMap<Object, HashMap<Object, Object>>> properties = new HashMap<>();
			HashMap<Object, HashMap<Object, Object>> typeAndValueMap = new HashMap<>();

			if (ldObj != null) {

				for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
					propertiesContent = iterator.next();

					if (propertiesContent.getValue() != null && propertiesContent.getValue() instanceof LinkedHashMap) {
						LinkedHashMap<Object, Object> linkedHashMap = (LinkedHashMap<Object, Object>) propertiesContent
								.getValue();
						if (linkedHashMap.containsKey("isLocation") && linkedHashMap.containsKey("coordinates")) {

							convertToGeoJson(value, (List<Object>) linkedHashMap.get("coordinates"),
									(String) linkedHashMap.get("typeGeolocation"));
							typeAndValueMap.put(propertiesContent.getKey(), value);
						}
					} else {
						value.put("type", "Property");
						value.put("value", propertiesContent.getValue());
						typeAndValueMap.put(propertiesContent.getKey(), value);
					}
					properties.put("properties", typeAndValueMap);
					value = new HashMap<>();
				}
			}

			linkedHashMapNGSILD.putAll(properties);

			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<>();
		}

		return listObjForSgeol;
	}

	public void convertToGeoJson(HashMap<Object, Object> value, List<Object> coordinates, String type) {
		value.put("type", "GeoProperty");
		HashMap<Object, Object> valueGeometry = new HashMap<>();
		valueGeometry.put("coordinates", coordinates);
		valueGeometry.put("type", type);
		value.put("value", valueGeometry);
	}

	@Override
	public void matchingWithContext(List<Object> listMatches, List<LinkedHashMap<Object, Object>> listNGSILD,
			HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LinkedHashMap<Object, Object>> converterToEntityNGSILD(LinkedHashMap<Object, Object> data,
			String layerNameFromSgeol, String entity, Map<Object, Object> contextLink) {
		// TODO Auto-generated method stub
		return null;
	}

}

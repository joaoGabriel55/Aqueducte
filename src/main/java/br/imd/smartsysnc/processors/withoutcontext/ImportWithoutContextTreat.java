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

		// Property data provided of SIGEduc API
		ArrayList<Object> listObjSigeduc = (ArrayList<Object>) data;

		List<LinkedHashMap<Object, Object>> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Entry<Object, Object> propertiesContent = null;

		List<String> contextList = Arrays.asList(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");

		for (int i = 0; i < listObjSigeduc.size(); i++) {
			LinkedHashMap<Object, Object> ldObj;
			ldObj = (LinkedHashMap<Object, Object>) listObjSigeduc.get(i);
			UUID uuid = UUID.randomUUID();
			NGSILDUtils.initDefaultProperties(linkedHashMapNGSILD, contextList, layerPath, uuid.toString());

			HashMap<Object, HashMap<Object, HashMap<Object, Object>>> properties = new HashMap<>();
			HashMap<Object, HashMap<Object, Object>> typeAndValueMap = new HashMap<>();

			if (ldObj != null) {

				for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
					propertiesContent = iterator.next();

					if (propertiesContent.getKey() == "localizacao" && propertiesContent.getValue() != null) {
						convertToGeoJson(value, propertiesContent);
						typeAndValueMap.put("location", value);
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

	public void convertToGeoJson(HashMap<Object, Object> value, Entry<Object, Object> propertiesContent) {
		value.put("type", "GeoProperty");
		HashMap<Object, Object> valueGeometry = new HashMap<>();
		String[] coordenates = propertiesContent.getValue().toString().split(",");
		valueGeometry.put("coordinates", coordenates);
		valueGeometry.put("type", "MultiPoint");
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

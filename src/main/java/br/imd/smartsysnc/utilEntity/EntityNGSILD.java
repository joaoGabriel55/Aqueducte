package br.imd.smartsysnc.utilEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

public class EntityNGSILD {

	/**
	 * @param data         - data provided by Quark Smart.
	 * @param ownLayerName - {@link Optional} - Define a specific name for layer.
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> converterToEntityNGSILD(LinkedHashMap<Object, Object> data, String ownLayerName,
			String entity) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();

		HashMap<Object, HashMap<Object, Object>> atributos = new HashMap<>();
		HashMap<Object, Object> value = new HashMap<>();

		String layerType = ownLayerName.length() != 0 ? ownLayerName : (String) data.get("name");

		// Property data provided of SIGEduc API
		ArrayList<Object> listObjSigeduc = (ArrayList<Object>) data.get("rows");

		List<Object> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Entry<Object, Object> properties = null;

		List<String> contextList = new ArrayList<>();

		contextList.add(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");
		contextList.add(
				"https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/education/school/School_Context.jsonld");

		for (int i = 0; i < listObjSigeduc.size(); i++) {
			linkedHashMapNGSILD.put("@context", contextList);
			// TODO Generate unique ID
			linkedHashMapNGSILD.put("id", "urn:ngsi-ld:layer:" + entity + ":id");
			linkedHashMapNGSILD.put("type", layerType);

			LinkedHashMap<Object, Object> ldObj = new LinkedHashMap<>();
			ldObj = (LinkedHashMap<Object, Object>) listObjSigeduc.get(i);

			for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
				properties = iterator.next();

				value.put("type", properties.getKey() != "geocode" ? "Property" : "GeoProperty");

				if (properties.getKey() == "geocode" && properties.getValue() != null) {
					HashMap<Object, Object> valueGeometry = new HashMap<>();
					String[] coordenates = (String[]) properties.getValue().toString().split(",");
					valueGeometry.put("coordinates", coordenates);
					valueGeometry.put("type", "MultiPoint");
					value.put("value", valueGeometry);
				} else {
					value.put("value", properties.getValue());
				}
				atributos.put(properties.getKey() != "geocode" ? properties.getKey() : "geometry", value);
				value = new HashMap<>();
			}
			// linkedHashMapNGSILD.put("properties", atributos);
			linkedHashMapNGSILD.putAll(atributos);
			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();
			atributos = new HashMap<Object, HashMap<Object, Object>>();
		}

		return listObjForSgeol;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> converterStateRNJsonToEntityNGSILD(List<Object> dataState) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();

		HashMap<Object, HashMap<Object, Object>> atributos = new HashMap<>();
		HashMap<Object, Object> value = new HashMap<>();

		List<Object> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Entry<Object, Object> properties = null;

		List<String> contextList = new ArrayList<>();

		contextList.add(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");
		contextList.add(
				"https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/education/school/School_Context.jsonld");

		for (int i = 0; i < dataState.size(); i++) {
			linkedHashMapNGSILD.put("@context", contextList);
//			// TODO Generate unique ID
//			HashMap<Object, Object> propertiesMunicipios = new HashMap<>();
//			propertiesMunicipios = (HashMap<Object, Object>) dataState.get(i);

			linkedHashMapNGSILD.put("id", "urn:ngsi-ld:layer:municipios:id");
			linkedHashMapNGSILD.put("type", "municipios");

			HashMap<Object, Object> ldObj = new HashMap<>();
			ldObj = (HashMap<Object, Object>) dataState.get(i);

			for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
				properties = iterator.next();
				value.put("type", !properties.getKey().equals("geometry") ? "Property" : "GeoProperty");

				if (properties.getKey().equals("geometry")) {
					HashMap<Object, Object> valueGeometry = new HashMap<>();
					HashMap<Object, Object> valueCoordinates = new HashMap<>();
					valueCoordinates = (HashMap<Object, Object>) properties.getValue();
					ArrayList<Object> listCoordinates = (ArrayList<Object>) valueCoordinates.get("coordinates");
					valueGeometry.put("coordinates", listCoordinates.get(0));
					valueGeometry.put("type", valueCoordinates.get("type"));
					value.put("value", valueGeometry);
					atributos.put(properties.getKey(), value);
				} else {
					if (properties.getKey().equals("properties")) {
						HashMap<Object, HashMap<Object, Object>> propertiesMunicipio = iterarPropertiesOfMunicipio(
								(HashMap<Object, Object>) properties.getValue());
						atributos.putAll(propertiesMunicipio);
					}
				}
				value = new HashMap<Object, Object>();
			}

			linkedHashMapNGSILD.putAll(atributos);
			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();
			atributos = new HashMap<Object, HashMap<Object, Object>>();
		}

		return listObjForSgeol;
	}

	private static HashMap<Object, HashMap<Object, Object>> iterarPropertiesOfMunicipio(
			HashMap<Object, Object> propertiesMunicipio) {
		HashMap<Object, HashMap<Object, Object>> atributos = new HashMap<>();

		Entry<Object, Object> properties = null;
		HashMap<Object, Object> value = new HashMap<>();
		for (Iterator<Entry<Object, Object>> iterator = propertiesMunicipio.entrySet().iterator(); iterator
				.hasNext();) {
			properties = iterator.next();

			value.put("type", "Property");
			value.put("value", properties.getValue());
			atributos.put(properties.getKey().equals("id") ? "id_municipio" : properties.getKey(), value);
			value = new HashMap<Object, Object>();
		}
		return atributos;
	}
}

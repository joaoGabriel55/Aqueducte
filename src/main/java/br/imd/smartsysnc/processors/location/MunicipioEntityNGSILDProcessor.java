package br.imd.smartsysnc.processors.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

public class MunicipioEntityNGSILDProcessor {

	@SuppressWarnings("unchecked")
	public List<Object> converterStateRNJsonToEntityNGSILD(List<Object> dataState) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

		HashMap<Object, HashMap<Object, HashMap<Object, Object>>> properties = new HashMap<>();
		HashMap<Object, HashMap<Object, Object>> atributos = new HashMap<>();
		HashMap<Object, Object> value = new HashMap<>();

		List<Object> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Entry<Object, Object> propertiesContent = null;

		List<String> contextList = Arrays.asList(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld",
				"https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/city/City_Context.jsonld");

		for (int i = 0; i < dataState.size(); i++) {
			linkedHashMapNGSILD.put("@context", contextList);
			UUID uuid = UUID.randomUUID();
			linkedHashMapNGSILD.put("id", "urn:ngsi-ld:layer:municipios:" + uuid.toString());
			linkedHashMapNGSILD.put("type", "municipio");

			HashMap<Object, Object> ldObj = new HashMap<>();
			ldObj = (HashMap<Object, Object>) dataState.get(i);

			for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
				propertiesContent = iterator.next();
				value.put("type", !propertiesContent.getKey().equals("geometry") ? "Property" : "GeoProperty");

				if (propertiesContent.getKey().equals("geometry")) {
					HashMap<Object, Object> valueGeometry = new HashMap<>();
					HashMap<Object, Object> valueCoordinates = new HashMap<>();
					valueCoordinates = (HashMap<Object, Object>) propertiesContent.getValue();
					ArrayList<Object> listCoordinates = (ArrayList<Object>) valueCoordinates.get("coordinates");
					valueGeometry.put("coordinates", listCoordinates.get(0));
					valueGeometry.put("type", "MultiPolygon"/* valueCoordinates.get("type") */);
					value.put("value", valueGeometry);
					atributos.put("location", value);
				} else {
					if (propertiesContent.getKey().equals("properties")) {
						HashMap<Object, HashMap<Object, Object>> propertiesMunicipio = iterarPropertiesOfMunicipio(
								(HashMap<Object, Object>) propertiesContent.getValue());
						atributos.putAll(propertiesMunicipio);
					}
				}
				properties.put("properties", atributos);
				value = new HashMap<>();
			}

			linkedHashMapNGSILD.putAll(atributos);
			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<>();
			atributos = new HashMap<>();
			properties = new HashMap<>();
		}

		return listObjForSgeol;
	}

	private HashMap<Object, HashMap<Object, Object>> iterarPropertiesOfMunicipio(
			HashMap<Object, Object> propertiesMunicipio) {
		HashMap<Object, HashMap<Object, Object>> atributos = new HashMap<>();

		Entry<Object, Object> properties = null;
		HashMap<Object, Object> value = new HashMap<>();
		for (Iterator<Entry<Object, Object>> iterator = propertiesMunicipio.entrySet().iterator(); iterator
				.hasNext();) {
			properties = iterator.next();

			value.put("type", "Property");
			value.put("value", properties.getValue());
			atributos.put(properties.getKey().equals("id") ? "id_geodata_br" : properties.getKey(), value);
			value = new HashMap<>();
		}
		return atributos;
	}

}

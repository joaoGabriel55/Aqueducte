package br.imd.smartsysnc.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EntityNGSILDUtils {

	/**
	 * @param data         - data provided by QuarkSmart.
	 * @param ownLayerName - {@link Optional} - Define a specific name for layer.
	 * @param entity       - {@link Optional} - Define the wished entity.
	 * @param contextLink  - Context for entity
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> converterToEntityNGSILD(LinkedHashMap<Object, Object> data, String ownLayerName,
			String entity, Map<Object, Object> contextLink) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();

		HashMap<Object, HashMap<Object, HashMap<Object, Object>>> properties = new HashMap<>();
		HashMap<Object, HashMap<Object, Object>> atributos = new HashMap<>();
		HashMap<Object, Object> value = new HashMap<>();

		String layerType = ownLayerName.length() != 0 ? ownLayerName : (String) data.get("name");

		// Property data provided of SIGEduc API
		ArrayList<Object> listObjSigeduc = (ArrayList<Object>) data.get("rows");

		List<Object> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Entry<Object, Object> propertiesContent = null;

		List<String> contextList = Arrays.asList(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld",
				contextLink.get("contextLink").toString());

		for (int i = 0; i < listObjSigeduc.size(); i++) {
			linkedHashMapNGSILD.put("@context", contextList);
			// TODO Generate unique ID
			UUID uuid = UUID.randomUUID();
			linkedHashMapNGSILD.put("id", "urn:ngsi-ld:" + layerType + ":" + uuid.toString());
			linkedHashMapNGSILD.put("type", layerType);

			LinkedHashMap<Object, Object> ldObj;
			ldObj = (LinkedHashMap<Object, Object>) listObjSigeduc.get(i);
			if (ldObj != null) {
				for (Iterator<Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
					propertiesContent = iterator.next();

					value.put("type", propertiesContent.getKey() != "localizacao" ? "Property" : "GeoProperty");

					if (propertiesContent.getKey() == "localizacao" && propertiesContent.getValue() != null) {
						HashMap<Object, Object> valueGeometry = new HashMap<>();
						String[] coordenates = (String[]) propertiesContent.getValue().toString().split(",");
						valueGeometry.put("coordinates", coordenates);
						valueGeometry.put("type", "MultiPoint");
						value.put("value", valueGeometry);
					} else {
						value.put("value", propertiesContent.getValue());
					}
					atributos.put(propertiesContent.getKey() != "localizacao" ? propertiesContent.getKey() : "location",
							value);
					properties.put("properties", atributos);
					value = new HashMap<>();
				}
			}
			linkedHashMapNGSILD.putAll(properties);
			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<>();
			atributos = new HashMap<>();
			properties = new HashMap<>();
		}

		return listObjForSgeol;
	}

	@SuppressWarnings("unchecked")
	public static Boolean isExistisEntity(String entity, String inepCode)
			throws UnsupportedEncodingException, IOException {

		HttpURLConnection con = RequestsUtils.sendRequest(
				RequestsUtils.URL_SGEOL + entity + "/find-by-query?query=p*.inep_id.value$eq$" + inepCode, "GET", true);
		ObjectMapper mapper = new ObjectMapper();
		if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
			String body = RequestsUtils.readBodyReq(con);
			Object credenciais = mapper.readValue(body, Object.class);
			if (((LinkedHashMap<Object, Object>) credenciais).entrySet().size() != 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public static List<Object> getColsFromSigEduc(LinkedHashMap<Object, Object> data) {
		// Property data provided of SIGEduc API
		ArrayList<Object> listColsSigeduc = (ArrayList<Object>) data.get("cols");
		return listColsSigeduc;
	}

	@SuppressWarnings("unchecked")
	public static void matchingWithContext(List<Object> listMatches, List<Object> listNGSILD,
			HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext) {
		for (Object element : listNGSILD) {

			HashMap<Object, Object> properties = (HashMap<Object, Object>) ((HashMap<Object, Object>) element)
					.get("properties");
			for (Entry<Object, Object> property : properties.entrySet()) {
				String key = property.getKey().toString();
				for (Object matches : listMatches) {
					LinkedHashMap<Object, Object> keyMatch = (LinkedHashMap<Object, Object>) matches;
					String eqPropertyKey = keyMatch.get("eqProperty").toString();
					String keyContext = keyMatch.get("name").toString();
					if (key.equals(eqPropertyKey) || key.equals(keyContext)) {
						if (((HashMap<Object, Object>) property.getValue()).get("value") != null
								&& ((HashMap<Object, Object>) property.getValue()).get("value") != ""  ) {
							propertiesBasedOnContext.put(keyContext, (HashMap<Object, Object>) property.getValue());
							break;
						}
					}
				}
			}
			((HashMap<Object, Object>) element).remove("properties");
			((HashMap<Object, Object>) element).putAll(propertiesBasedOnContext);
			propertiesBasedOnContext = new HashMap<>();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<Object> converterStateRNJsonToEntityNGSILD(List<Object> dataState) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();

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
			// TODO Generate unique ID
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
				value = new HashMap<Object, Object>();
			}

			linkedHashMapNGSILD.putAll(atributos);
//			linkedHashMapNGSILD.putAll(properties);
			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<Object, Object>();
			atributos = new HashMap<Object, HashMap<Object, Object>>();
			properties = new HashMap<Object, HashMap<Object, HashMap<Object, Object>>>();
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
			atributos.put(properties.getKey().equals("id") ? "id_geodata_br" : properties.getKey(), value);
			value = new HashMap<Object, Object>();
		}
		return atributos;
	}
}

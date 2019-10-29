package br.imd.aqueducte.processors.impl;

import static br.imd.aqueducte.utils.RequestsUtils.APP_TOKEN;
import static br.imd.aqueducte.utils.RequestsUtils.USER_TOKEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.imd.aqueducte.processors.NGSILDTreat;
import br.imd.aqueducte.utils.NGSILDUtils;

public class NGSILDTreatImpl implements NGSILDTreat {
	@SuppressWarnings("unchecked")
	@Override
	public List<LinkedHashMap<Object, Object>> convertToEntityNGSILD(List<Object> data, String layerPath,
			Map<Object, Object> contextLink) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

		HashMap<Object, Object> value = new HashMap<>();

		// Property data provided from external API
		ArrayList<Object> dataListFromExternalAPI = (ArrayList<Object>) data;

		List<LinkedHashMap<Object, Object>> listObjForSgeol = new ArrayList<>();

		// Attribute of entity obtained.
		Map.Entry<Object, Object> propertiesContent = null;

		List<String> contextList = Arrays.asList(
				"https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");

		for (int i = 0; i < dataListFromExternalAPI.size(); i++) {
			LinkedHashMap<Object, Object> ldObj;
			ldObj = (LinkedHashMap<Object, Object>) dataListFromExternalAPI.get(i);
			UUID uuid = UUID.randomUUID();
			NGSILDUtils.initDefaultProperties(linkedHashMapNGSILD, contextList, layerPath, uuid.toString());

			HashMap<Object, HashMap<Object, Object>> typeAndValueMap = new HashMap<>();

			if (ldObj != null) {
				for (Iterator<Map.Entry<Object, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext();) {
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
						if (propertiesContent.getKey() == "type" || propertiesContent.getKey() == "id") {
							typeAndValueMap.put(propertiesContent.getKey() + "_", value);
						} else if (propertiesContent.getKey().toString().contains(".")) {
							typeAndValueMap.put(propertiesContent.getKey().toString().replace(".", "_"), value);
						} else {
							typeAndValueMap.put(propertiesContent.getKey(), value);
						}
					}
					value = new HashMap<>();
				}
			}
			linkedHashMapNGSILD.putAll(typeAndValueMap);
			listObjForSgeol.add(linkedHashMapNGSILD);
			linkedHashMapNGSILD = new LinkedHashMap<>();
		}

		return listObjForSgeol;
	}

	public void convertToGeoJson(HashMap<Object, Object> value, List<Object> coordinates, String type) {
		value.put("type", "GeoProperty");
		HashMap<Object, Object> valueGeometry = new HashMap<>();

		if (type != "Point")
			valueGeometry.put("coordinates", parseDoubleCoordinates(coordinates));
		else
			valueGeometry.put("coordinates", parseDoubleCoordinates(coordinates).get(0));

		valueGeometry.put("type", type);
		value.put("value", valueGeometry);
	}

	private List<List<Object>> parseDoubleCoordinates(List<Object> coordinates) {
		List<Object> coordinatesDoubleType = new ArrayList<>();
		List<List<Object>> coordinatesFinal = new ArrayList<>();
		for (Object coordinate : coordinates) {
			coordinatesDoubleType.add(Double.parseDouble(coordinate.toString().replace(",", ".")));
		}
		coordinatesFinal.add(coordinatesDoubleType);
		return coordinatesFinal;
	}

	@Override
	public List<String> importToSGEOL(String url, String appToken, String userToken, JSONArray jsonArray) {
		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `accept` header
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		// set custom header
		headers.set(APP_TOKEN, appToken);
		headers.set(USER_TOKEN, userToken);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		// use `exchange` method for HTTP call
		List<String> jsonArrayResponse = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			HttpEntity<String> entity = new HttpEntity<>(jsonArray.get(i).toString(), headers);
			ResponseEntity<String> responseSGEOL = restTemplate.exchange(url, HttpMethod.POST, entity,
					new ParameterizedTypeReference<String>() {
					});
			if (responseSGEOL.getStatusCode() == HttpStatus.CREATED) {
				jsonArrayResponse.add(jsonObject.get("id").toString());
			}
		}
		return jsonArrayResponse;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void matchingWithContext(List<Object> listMatches, List<LinkedHashMap<Object, Object>> listNGSILD,
			HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext) {
		for (Object element : listNGSILD) {

			HashMap<Object, Object> objNGSILD = (HashMap<Object, Object>) ((HashMap<Object, Object>) element)
					.get("objNGSILD");
			HashMap<Object, Object> properties = (HashMap<Object, Object>) objNGSILD.get("properties");

			for (Entry<Object, Object> property : properties.entrySet()) {
				String key = property.getKey().toString();
				for (Object matches : listMatches) {
					LinkedHashMap<Object, Object> keyMatch = (LinkedHashMap<Object, Object>) matches;
					String eqPropertyKey = keyMatch.get("eqProperty").toString();
					String keyContext = keyMatch.get("name").toString();
					if (key.equals(eqPropertyKey) || key.equals(keyContext)) {
						if (((HashMap<Object, Object>) property.getValue()).get("value") != null
								&& ((HashMap<Object, Object>) property.getValue()).get("value") != "") {
							propertiesBasedOnContext.put(keyContext, (HashMap<Object, Object>) property.getValue());
							break;
						}
					}
				}
			}
			((HashMap<Object, Object>) element).remove("properties");
			objNGSILD.put("properties", propertiesBasedOnContext);
			propertiesBasedOnContext = new HashMap<>();
		}
	}

	@Override
	public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport) {
		return null;
	}
}

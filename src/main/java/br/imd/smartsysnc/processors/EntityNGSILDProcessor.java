package br.imd.smartsysnc.processors;

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

import br.imd.smartsysnc.utils.RequestsUtils;

public class EntityNGSILDProcessor {

	/**
	 * @param data         - data provided by QuarkSmart.
	 * @param ownLayerName - {@link Optional} - Define a specific name for layer.
	 * @param entity       - {@link Optional} - Define the wished entity.
	 * @param contextLink  - Context for entity
	 */
	@SuppressWarnings("unchecked")
	public List<Object> converterToEntityNGSILD(LinkedHashMap<Object, Object> data, String ownLayerName, String entity,
			Map<Object, Object> contextLink) {

		LinkedHashMap<Object, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

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
						String[] coordenates = propertiesContent.getValue().toString().split(",");
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
	public Boolean isExistisEntity(String entity, String inepCode)
			throws UnsupportedEncodingException, IOException {

		HttpURLConnection con = RequestsUtils.sendRequest(
				RequestsUtils.URL_SGEOL + entity + "/find-by-query?query=p*.inep_id.value$eq$" + inepCode, "GET", true);
		ObjectMapper mapper = new ObjectMapper();
		if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
			String body = RequestsUtils.readBodyReq(con);
			ArrayList<LinkedHashMap<Object, Object>> credenciais = mapper.readValue(body, ArrayList.class);
//			((LinkedHashMap<Object, Object>) credenciais).entrySet().size() != 0
			if (credenciais.size() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Object> getColsFromSigEduc(LinkedHashMap<Object, Object> data) {
		// Property data provided of SIGEduc API
		ArrayList<Object> listColsSigeduc = (ArrayList<Object>) data.get("cols");
		return listColsSigeduc;
	}

	@SuppressWarnings("unchecked")
	public  void matchingWithContext(List<Object> listMatches, List<Object> listNGSILD,
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
								&& ((HashMap<Object, Object>) property.getValue()).get("value") != "") {
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
}

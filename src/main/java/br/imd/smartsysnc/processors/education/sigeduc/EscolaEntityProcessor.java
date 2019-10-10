package br.imd.smartsysnc.processors.education.sigeduc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.processors.Processor;
import br.imd.smartsysnc.utils.FormatterUtils;
import br.imd.smartsysnc.utils.RequestsUtils;

public class EscolaEntityProcessor implements Processor {

	@Override
	public Map<Object, Object> getIdsForRelationship(Map<Object, Object> ldObj) {
		Map<Object, Object> idEscola = ldObj.entrySet().stream()
				.filter(x -> x.getKey() == "id_escola" || x.getKey() == "codigo_inep")
				.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue() != null ? x.getValue() : ""));

//		Map<Object, Object> idInep = ldObj.entrySet().stream().filter(x -> x.getKey() == "codigo_inep")
//				.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue() != null ? x.getValue() : ""));
//
//		Map<Object, Object> map = new HashMap<>();
//		if (idInep != null)
//			map.putAll(idInep);
//		map.putAll(idEscola);

		return idEscola;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> ldObj) {

		Map<Object, Object> objNGSILD = (LinkedHashMap<Object, Object>) ldObj.get("objNGSILD");
		Map<Object, Object> propertiesObjNGSILD = (LinkedHashMap<Object, Object>) objNGSILD.get("properties");

		Map<Object, Object> idForRelationShip = (LinkedHashMap<Object, Object>) ldObj.get("idForRelationship");

		try {
			String inepCode = (String) FormatterUtils
					.getValuePropertyNGSILD((Map<Object, Object>) propertiesObjNGSILD.get("inepCodSch"));
			if (isExistisEntityByInepCode(objNGSILD.get("type").toString(), inepCode)) {
				Map<Object, Object> idsLinked = new HashMap<>();
				idsLinked.put("idSGEOL", objNGSILD.get("id").toString());
				idsLinked.put("idForRelationShip", idForRelationShip);
				return idsLinked;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private boolean isExistisEntityByInepCode(String entity, String inepCode) throws IOException {
		if (inepCode != null) {

			HttpURLConnection con = RequestsUtils.sendRequest(
					RequestsUtils.URL_SGEOL + entity + "/find-by-query?query=p*.inep_id.value$eq$" + inepCode, "GET",
					false);
			ObjectMapper mapper = new ObjectMapper();
			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con.getInputStream());
				ArrayList<LinkedHashMap<Object, Object>> credenciais = mapper.readValue(body, ArrayList.class);
				if (!credenciais.isEmpty()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}

}

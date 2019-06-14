package br.imd.smartsysnc.processors.sigeduc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.processors.Processor;
import br.imd.smartsysnc.utils.RequestsUtils;

public class EscolaEntityProcessor extends Processor {

	public EscolaEntityProcessor(LinkedHashMap<Object, Object> ldObj, String entity, int operation) {
		super(ldObj, entity, operation);
	}

//	public void execute(Map<Object, Object> entidadeToImport, String entity, List<Map<String, Object>> listIDsLinked)
//			throws Exception {
//		if (entidadeToImport.containsKey("inepCod")) {
//			@SuppressWarnings("unchecked")
//			String inepCode = (String) ((LinkedHashMap<Object, Object>) entidadeToImport.get("inepCod")).get("value");
//
//			List<Map<String, Object>> listIdsRelationships = new ArrayList<>();
//
///*			SigEducAPIEntityUtils sigEducAPIEntityUtils = new SigEducAPIEntityUtils();
//			List<Object> listIds = sigEducAPIEntityUtils.getColsFromSigEduc(entity);
//*/
//			listIdsRelationships.add(null);
//
//			Boolean isExistis = isExistisEntityByInepCode(entity, inepCode);
//			if (isExistis != null && !isExistis) {
//				importToSGEOL(entidadeToImport, entity, listIDsLinked, listIdsRelationships);
//			}
//		}
//	}

	public LinkedHashMap<Object, Object> getIdsForRelationship(LinkedHashMap<Object, Object> ldObj){
		return ldObj;
	}

	@SuppressWarnings("unchecked")
	private boolean isExistisEntityByInepCode(String entity, String inepCode)
			throws UnsupportedEncodingException, IOException {

		HttpURLConnection con = RequestsUtils.sendRequest(
				RequestsUtils.URL_SGEOL + entity + "/find-by-query?query=p*.inep_id.value$eq$" + inepCode, "GET", true);
		ObjectMapper mapper = new ObjectMapper();
		if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
			String body = RequestsUtils.readBodyReq(con);
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

}

package br.imd.smartsysnc.processors.sigeduc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.utils.RequestsUtils;

public class SigEducAPIEntityUtils {

	@SuppressWarnings("unchecked")
	public List<Object> getColsFromSigEduc(String entity) throws Exception {
		// Property data provided of SIGEduc API
		String baseUrl = RequestsUtils.URL_SIGEDUC + entity + "?limit=1";
		try {

			HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
			ObjectMapper mapper = new ObjectMapper();

			if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
				String body = RequestsUtils.readBodyReq(con);
				Object credenciais = mapper.readValue(body, Object.class);

				return (List<Object>) ((LinkedHashMap<Object, Object>) credenciais).get("cols");
			}
		} catch (IOException e) {
			throw new Exception();
		}
		return new ArrayList<>();
	}

}
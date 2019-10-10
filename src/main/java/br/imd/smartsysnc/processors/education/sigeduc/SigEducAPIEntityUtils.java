package br.imd.smartsysnc.processors.education.sigeduc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.smartsysnc.utils.RequestsUtils;

public class SigEducAPIEntityUtils {

    public static String getUrlOfEntity(String entity, int limit, int offset) {
        return RequestsUtils.URL_SIGEDUC + entity + "?limit=" + limit + "&offset=" + offset;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getColsFromSigEduc(String entity) throws Exception {
        // Property data provided of SIGEduc API
        String baseUrl = RequestsUtils.URL_SIGEDUC + entity + "?limit=1";
        try {

            HttpURLConnection con = RequestsUtils.sendRequest(baseUrl, "GET", true);
            ObjectMapper mapper = new ObjectMapper();

            if (con.getResponseCode() == RequestsUtils.STATUS_OK) {
                String body = RequestsUtils.readBodyReq(con.getInputStream());
                Object credenciais = mapper.readValue(body, Object.class);

                return (List<Object>) ((LinkedHashMap<Object, Object>) credenciais).get("cols");
            }
        } catch (IOException e) {
            throw new Exception();
        }
        return new ArrayList<>();
    }

}
package br.imd.smartsysnc.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestsUtils {
    public static int STATUS_OK = 200;
    private static String TOKEN = "dd0ce06d6d51c059f52924aefb2a6aad";

    public static String URL_SIGEDUC = "https://quarksmart.com.br/api/v1/dw/entity/";
    //	public static String URL_SGEOL = "http://localhost:8091/data-middleware/v2/"; // Local
    public static String URL_SGEOL = "http://10.7.52.26:8080/sgeol-dm/v2/"; // Test;
//	private static String URL_SGEOL = "http://10.7.52.76:8080/sgeol-dm/v2/"; // Production;

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static HttpURLConnection sendRequest(String baseUrl, String method, boolean needToken) throws IOException {
        URL url = new URL(baseUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod(method);
        con.setConnectTimeout(10000);
        con.setRequestProperty("Content-Type", "application/json");
        if (needToken)
            con.setRequestProperty("TOKEN", TOKEN);

        con.connect();
        return con;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> requestToAPI(Map<Object, Object> paramsRequest) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if (paramsRequest.get("method").toString().equalsIgnoreCase("GET"))
            response = httpGet((LinkedHashMap<Object, Object>) paramsRequest, fullUrl(paramsRequest));
        else if (paramsRequest.get("method").toString().equalsIgnoreCase("POST"))
            response = httpPost(paramsRequest.get("data"), fullUrl(paramsRequest));
        return response;
    }

    private static Map<String, Object> httpGet(LinkedHashMap<Object, Object> paramsRequest, String fullUrl) throws IOException {
        URL url = new URL(fullUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("GET");
        con.setConnectTimeout(10000);

        LinkedHashMap<String, String> headersParams = (LinkedHashMap<String, String>) paramsRequest.get("headers");
        if (headersParams != null) {
            setHeadersParams(headersParams, con);
        }

        if (paramsRequest.get("data") != null)
            treatBodyData(con, paramsRequest.get("data").toString());

        con.connect();
        Map<String, Object> response = buildResponse(con.getResponseCode(), con.getResponseMessage(), con.getInputStream());
        return response;
    }

    private static Map<String, Object> httpPost(Object paramsRequest, String url) throws IOException {
        String jsonString = null;
        if (!(paramsRequest instanceof ArrayList))
            jsonString = new JSONObject(paramsRequest).toString();
        else
            jsonString = paramsRequest.toString();

        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        // TODO add headers
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        Map<String, Object> finalResponse = buildResponse(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), response.getEntity().getContent());
        return finalResponse;
    }

    private static Map<String, Object> buildResponse(int statusCode, String statusMessage, InputStream inputStream) throws IOException {
        Map<String, Object> responseObject = new HashMap<>();

        if (statusCode == STATUS_OK) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> jsonObject = mapper.readValue("{ \"data\": " + readBodyReq(inputStream) + "}", Map.class);
            responseObject.putAll(jsonObject);
            return responseObject;
        }
        responseObject.put("statusCode", statusCode);
        responseObject.put("message", statusMessage);

        return responseObject;
    }

    @SuppressWarnings("unchecked")
    private static String fullUrl(Map<Object, Object> paramsResquest) {
        String url = paramsResquest.get("url").toString();

        LinkedHashMap<String, String> queryParams = (LinkedHashMap<String, String>) paramsResquest.get("params");

        if (queryParams != null && queryParams.size() > 0)
            return url + "?" + getQueryParams(queryParams);
        return url;
    }

    private static String getQueryParams(LinkedHashMap<String, String> queryParams) {
        String queryParamsString = "";
        int count = 1;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (count == queryParams.entrySet().size())
                queryParamsString += key + "=" + value;
            else
                queryParamsString += key + "=" + value + "&";
            count++;
        }

        return queryParamsString;

    }

    private static void setHeadersParams(LinkedHashMap<String, String> headersParams, HttpURLConnection con) {
        for (Map.Entry<String, String> entry : headersParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            con.setRequestProperty(key, value);
        }
    }

    public static void treatBodyData(HttpURLConnection con, String jsonInputString) throws IOException {
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    public static String readBodyReq(InputStream inputStream) throws IOException {
        /* Lendo body */
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String body = "";
        String temp = null;
        while ((temp = br.readLine()) != null)
            body += temp;

        return body;
    }

    public static void postMethodRestTemplate(String url, Map<Object, Object> entidadeToImport) {

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new StringHttpMessageConverter());

        rt.postForEntity(url, entidadeToImport, String.class);
    }

}

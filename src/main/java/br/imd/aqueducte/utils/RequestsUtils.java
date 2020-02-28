package br.imd.aqueducte.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static br.imd.aqueducte.utils.PropertiesParams.STATUS_OK;

public class RequestsUtils {

    public String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> requestToAPI(Map<Object, Object> paramsRequest) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if (paramsRequest.get("method").toString().equalsIgnoreCase("GET")) {
            response = httpGet(
                    fullUrl(paramsRequest),
                    (LinkedHashMap<String, String>) paramsRequest.get("headers")
            );
        } else if (paramsRequest.get("method").toString().equalsIgnoreCase("POST")) {
            response = httpPost(
                    fullUrl(paramsRequest),
                    paramsRequest.get("data"),
                    (LinkedHashMap<String, String>) paramsRequest.get("headers")
            );
        }
        return response;
    }

    private Map<String, Object> httpGet(String fullUrl, LinkedHashMap<String, String> headersParams) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(fullUrl);

        setHeadersParams(headersParams, request);

        HttpResponse response = httpClient.execute(request);
        return buildResponse(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), response.getEntity().getContent());
    }

    public Map<String, Object> httpPost(String url, Object paramsRequest, LinkedHashMap<String, String> headersParams) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        setHeadersParams(headersParams, request);
        request.setEntity(buildEntity(paramsRequest));

        HttpResponse response = httpClient.execute(request);
        return buildResponse(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), response.getEntity().getContent());
    }

    public void setHeadersParams(LinkedHashMap<String, String> headersParams, HttpRequestBase request) {
        if (headersParams != null) {
            for (Map.Entry<String, String> entry : headersParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                request.setHeader(key, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> buildResponse(int statusCode, String statusMessage, InputStream inputStream) throws IOException {
        Map<String, Object> responseObject = new HashMap<>();

        if (statusCode == STATUS_OK) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonObject = mapper.readValue("{ \"data\": " + readBodyReq(inputStream) + "}", Map.class);
            responseObject.putAll(jsonObject);
            return responseObject;
        }
        responseObject.put("statusCode", statusCode);
        responseObject.put("message", statusMessage);

        return responseObject;
    }

    @SuppressWarnings("unchecked")
    private String fullUrl(Map<Object, Object> paramsRequest) {
        String url = paramsRequest.get("url").toString();

        LinkedHashMap<String, String> queryParams = (LinkedHashMap<String, String>) paramsRequest.get("params");

        if (queryParams != null && queryParams.size() > 0)
            return url + "?" + getQueryParams(queryParams);
        return url;
    }

    private String getQueryParams(LinkedHashMap<String, String> queryParams) {
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

    public void treatBodyData(HttpURLConnection con, String jsonInputString) throws IOException {
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
    }

    public static String readBodyReq(InputStream inputStream) throws IOException {
        /* Lendo body */
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String body = "";
        String temp = null;
        while ((temp = br.readLine()) != null)
            body += temp;

        return body;
    }

    public void postMethodRestTemplate(String url, Map<Object, Object> entidadeToImport) {

        RestTemplate rt = new RestTemplate();
        rt.getMessageConverters().add(new StringHttpMessageConverter());

        rt.postForEntity(url, entidadeToImport, String.class);
    }

    public StringEntity buildEntity(Object data) {
        String jsonString;
        if (!(data instanceof ArrayList))
            jsonString = new JSONObject(data).toString();
        else
            jsonString = data.toString();

        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        return entity;
    }

}

package br.imd.aqueducte.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RequestsUtils {

    public static HttpClient getHttpClientInstance() {
        HttpClient httpClientInstance = null;
        TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            httpClientInstance = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return httpClientInstance;
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
        HttpGet request = new HttpGet(fullUrl);

        setHeadersParams(headersParams, request);

        HttpResponse response = getHttpClientInstance().execute(request);
        return buildResponse(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), response.getEntity().getContent());
    }

    public Map<String, Object> httpPost(String url, Object paramsRequest, LinkedHashMap<String, String> headersParams) throws IOException {
        HttpPost request = new HttpPost(url);

        setHeadersParams(headersParams, request);
        request.setEntity(buildEntity(paramsRequest));

        HttpResponse response = getHttpClientInstance().execute(request);
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

        if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
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
        StringBuilder queryParamsString = new StringBuilder();
        int count = 1;
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (count == queryParams.entrySet().size())
                queryParamsString.append(key).append("=").append(value);
            else
                queryParamsString.append(key).append("=").append(value).append("&");
            count++;
        }

        return queryParamsString.toString();

    }

    public static String readBodyReq(InputStream inputStream) throws IOException {
        /* Reading body */
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder body = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null)
            body.append(temp);

        return body.toString();
    }

    public StringEntity buildEntity(Object data) {
        String jsonString;
        if (!(data instanceof ArrayList) && !(data instanceof JSONArray))
            jsonString = new JSONObject(data).toString();
        else
            jsonString = data.toString();

        return new StringEntity(jsonString, ContentType.APPLICATION_JSON);
    }

}

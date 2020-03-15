package br.imd.aqueducte.entitiesrelationship.queriesservices;

import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.imd.aqueducte.utils.PropertiesParams.URL_SGEOL;
import static br.imd.aqueducte.utils.RequestsUtils.readBodyReq;

@SuppressWarnings("ALL")
public class EntityOperationsService {

    public static final String FIND_ENTITY_BY_DOCUMENT = "/find-by-document";
    private static final String FIND_ENTITY_BY_ID = "/find-by-id";
    private static final String PREPROCESSING_LAYER_ENTITIES = "preprocessing/";

    private static final HttpClient HTTP_CLIENT_INSTANCE = RequestsUtils.getHttpClientInstance();

    private static EntityOperationsService instance;

    public static EntityOperationsService getInstance() {
        if (instance == null) {
            instance = new EntityOperationsService();
        }
        return instance;
    }

    public List<Map<String, Object>> getEntitiesPageable(String layer, int limit, int offset) throws Exception {
        HttpGet request = new HttpGet(URL_SGEOL + layer + "?limit=" + limit + "&offset=" + offset);
        try {
            HttpResponse response = HTTP_CLIENT_INSTANCE.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ObjectMapper mapper = new ObjectMapper();
                Object jsonArray = mapper.readValue(
                        readBodyReq(response.getEntity().getContent()), Object.class
                );
                if (jsonArray instanceof List)
                    return (List<Map<String, Object>>) jsonArray;
                return new ArrayList<>();
            }
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }

    }

    public Map<String, Object> findEntityById(String layer, String id) {
        String uri = URL_SGEOL + layer + FIND_ENTITY_BY_ID + "?entity-id=" + id;
        HttpGet request = new HttpGet(uri);
        try {
            HttpResponse response = HTTP_CLIENT_INSTANCE.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> entity = mapper.readValue(
                        readBodyReq(response.getEntity().getContent()), Map.class
                );
                if (entity.size() == 0 || entity == null)
                    return null;
                return entity;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> findByDocument(String layer,
                                       String propertyName,
                                       Object value,
                                       boolean isTempProperty,
                                       String appToken,
                                       String userToken
    ) {
        String query = "{\"$and\": [{\"properties." + propertyName + ".value\": {\"$eq\": " + value + "}}]}";
        if (isTempProperty)
            query = "{\"$and\": [{\"properties." + propertyName + ".value.value\": {\"$eq\": " + value + "}}]}";

        JSONObject queryJson = new JSONObject(query);
        return executeServiceFindByDocument(layer, queryJson, appToken, userToken);
    }

    private List<String> executeServiceFindByDocument(String layer, JSONObject query, String appToken, String userToken) {
        HttpPost request = new HttpPost(URL_SGEOL + layer + FIND_ENTITY_BY_DOCUMENT);
        StringEntity entity = new StringEntity(query.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            HttpResponse response = HTTP_CLIENT_INSTANCE.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ObjectMapper mapper = new ObjectMapper();
                List<Map<String, Object>> jsonArray = mapper.readValue(
                        readBodyReq(response.getEntity().getContent()), List.class
                );
                List<String> entitiesId = jsonArray
                        .stream()
                        .map((elem) -> elem.get("id").toString())
                        .collect(Collectors.toList());

                return entitiesId;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
//        create headers
//        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
//        headers.put(APP_TOKEN, appToken);
//        headers.put(USER_TOKEN, userToken);
//        requestsUtils.setHeadersParams(headers, request);

    }

    public boolean deleteEntityTempProperty(String layer, String entityId, String propertyName) throws Exception {
        String uri = URL_SGEOL + layer + "/property?entity-id=" + entityId + "&property-name=" + propertyName;
        HttpDelete request = new HttpDelete(uri);
        try {
            HttpResponse response = HTTP_CLIENT_INSTANCE.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return true;
            else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public boolean deleteDataFromPreprocessingLayer(String preprocessinglayer) throws Exception {
        String uri = URL_SGEOL + PREPROCESSING_LAYER_ENTITIES + preprocessinglayer;
        HttpDelete request = new HttpDelete(uri);
        try {
            HttpResponse response = HTTP_CLIENT_INSTANCE.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return true;
            else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

}

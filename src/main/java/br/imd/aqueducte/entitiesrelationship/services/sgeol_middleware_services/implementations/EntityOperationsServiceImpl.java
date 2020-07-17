package br.imd.aqueducte.entitiesrelationship.services.sgeol_middleware_services.implementations;

import br.imd.aqueducte.entitiesrelationship.services.sgeol_middleware_services.EntityOperationsService;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.imd.aqueducte.utils.FormatterUtils.treatPrimaryField;
import static br.imd.aqueducte.utils.RequestsUtils.*;

@SuppressWarnings("ALL")
@Service
public class EntityOperationsServiceImpl implements EntityOperationsService {

    public static final String FIND_ENTITY_BY_DOCUMENT = "/find-by-document";
    private static final String FIND_ENTITY_BY_ID = "/find-by-id";
    private static final String PREPROCESSING_LAYER_ENTITIES = "preprocessing/";
    private static final String TRANSFER_LAYER_ENTITIES = "preprocessing/transfer-layer-entities/";
    private static final String CONTAINED_IN = "/contained-in";

    @Override
    public List<Map<String, Object>> getEntitiesPageable(String sgeolInstance, String appToken, String userToken, String layer, int limit, int offset) throws Exception {
        HttpGet request = new HttpGet(sgeolInstance + "/v2/" + layer + "?limit=" + limit + "&offset=" + offset);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
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

    @Override
    public Map<String, Object> findEntityById(String sgeolInstance, String appToken, String userToken, String layer, String id) {
        String uri = sgeolInstance + "/v2/" + layer + FIND_ENTITY_BY_ID + "?entity-id=" + id;
        HttpGet request = new HttpGet(uri);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
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

    @Override
    public List<String> findByDocument(String sgeolInstance,
                                       String layer,
                                       String propertyName,
                                       Object value,
                                       boolean isTempProperty,
                                       String appToken,
                                       String userToken
    ) {
        String query = "{\"$and\": [{\"properties." + treatPrimaryField(propertyName) + ".value\": {\"$eq\": " + value + "}}]}";
        if (isTempProperty)
            query = "{\"$and\": [{\"properties." + treatPrimaryField(propertyName) + ".value.value\": {\"$eq\": " + value + "}}]}";

        JSONObject queryJson = new JSONObject(query);
        return executeServiceFindByDocument(sgeolInstance, layer, queryJson, appToken, userToken);
    }

    private List<String> executeServiceFindByDocument(String sgeolInstance, String layer, JSONObject query, String appToken, String userToken) {
        HttpPost request = new HttpPost(sgeolInstance + "/v2/" + layer + FIND_ENTITY_BY_DOCUMENT);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        StringEntity entity = new StringEntity(query.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
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
    }

    @Override
    public boolean updateEntity(String sgeolInstance, String id, String appToken, String userToken, LinkedHashMap<String, Object> entity, String layer) {
        entity.put("id", id);
        JSONObject entityJson = new JSONObject(entity);
        HttpPut request = new HttpPut(sgeolInstance + "/v2/" + layer);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        StringEntity stringEntity = new StringEntity(entityJson.toString(), ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method call the SGEOL contained-in service. That service, look entities contained in a specific layer,
     * using GeoProperties
     */
    @Override
    public List<String> findContainedIn(String sgeolInstance,
                                        String layer,
                                        String containerLayer,
                                        String containerEntityId,
                                        int limit,
                                        int offset,
                                        String appToken,
                                        String userToken) {
        String uri = sgeolInstance + "/v2/" + layer + CONTAINED_IN +
                "?container-layer=" + containerLayer +
                "&container-entity=" + containerEntityId +
                "&limit=" + limit +
                "&offset=" + offset;
        HttpGet request = new HttpGet(uri);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
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
    }

    @Override
    public boolean transferPreprocessingLayerEntitiesToFinalLayer(String sgeolInstance, String appToken, String userToken, String preprocessingLayer, String finalLayer) throws IOException {
        HttpPost request = new HttpPost(
                sgeolInstance + "/v2/" + TRANSFER_LAYER_ENTITIES + preprocessingLayer + "/" + finalLayer
        );
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }

    }


    @Override
    public boolean deleteEntityTempProperty(String sgeolInstance, String appToken, String userToken, String layer, String entityId, String propertyName) throws Exception {
        String uri = sgeolInstance + "/v2/" + layer + "/property?entity-id=" + entityId + "&property-name=" + propertyName;
        HttpDelete request = new HttpDelete(uri);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return true;
            else
                return false;

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    @Override
    public boolean deleteDataFromPreprocessingLayer(String sgeolInstance, String appToken, String userToken, String preprocessinglayer) throws Exception {
        String uri = sgeolInstance + "/v2/" + PREPROCESSING_LAYER_ENTITIES + preprocessinglayer;
        HttpDelete request = new HttpDelete(uri);
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        RequestsUtils requestsUtils = new RequestsUtils();
        requestsUtils.setHeadersParams(headers, request);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
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

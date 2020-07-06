package br.imd.aqueducte.services.sgeolqueriesservices;

import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static br.imd.aqueducte.services.sgeolqueriesservices.EntityOperationsService.FIND_ENTITY_BY_DOCUMENT;
import static br.imd.aqueducte.utils.RequestsUtils.*;

@SuppressWarnings("ALL")
public class RelationshipOperationsService {

    private static final String RELATIONSHIP = "/relationship";
    private static final String FIND_BY_RELATIONSHIP_FILTER = "/find-by-relationship-filter";

    private static RelationshipOperationsService instance;

    public static RelationshipOperationsService getInstance() {
        if (instance == null) {
            instance = new RelationshipOperationsService();
        }
        return instance;
    }

    public boolean relationshipEntities(
            String sgeolInstance,
            String appToken,
            String userToken,
            String layer1,
            String layer2,
            String idFromLayerEntity1,
            String idFromLayerEntity2,
            Map<String, String> relationships
    ) throws Exception {
        try {
            boolean statusOperationRelationship1 = false;
            boolean statusOperationRelationship2 = false;
            if (relationships.containsKey(layer1) && !relationships.get(layer1).equals("")) {
                statusOperationRelationship1 = executeRelationship(
                        sgeolInstance, appToken, userToken, layer1, layer2, idFromLayerEntity1, idFromLayerEntity2, relationships
                );
            }
            if (relationships.containsKey(layer2) && !relationships.get(layer2).equals("")) {
                statusOperationRelationship2 = executeRelationship(
                        sgeolInstance, appToken, userToken, layer2, layer1, idFromLayerEntity2, idFromLayerEntity1, relationships
                );
            }

            if (statusOperationRelationship1 || statusOperationRelationship2)
                return true;
        } catch (
                Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
        return false;
    }

    private boolean executeRelationship(
            String sgeolInstance,
            String appToken,
            String userToken,
            String layer1,
            String layer2,
            String idFromLayerEntity1,
            String idFromLayerEntity2,
            Map<String, String> relationships) throws Exception {
        boolean statusOperationRelationship = false;
        List<String> objectId = new ArrayList<>();

        Map<String, Object> relationship = getEntityRelationshipsObjectIds(
                sgeolInstance, appToken, userToken, layer1, idFromLayerEntity1, relationships.get(layer1)
        );

        if (relationship != null) {
            boolean hasObjectId = false;
            boolean isString = false;
            if (relationship.get("object") instanceof String)
                isString = true;
            else if (relationship.get("object") instanceof Collection)
                isString = false;

            hasObjectId = hasRelationshipObjectId(
                    sgeolInstance,
                    appToken, userToken,
                    layer1, idFromLayerEntity1,
                    relationships.get(layer1),
                    idFromLayerEntity2, isString
            );

            if (!hasObjectId || (relationship.containsKey("object") && relationship.get("object") != null))
                objectId = addObjectIdIntoRelationship(relationship.get("object"), idFromLayerEntity2);
            else
                objectId = null;
        }

        try {
            statusOperationRelationship = addOrUpdateRelationshipIntoEntity(
                    sgeolInstance,
                    appToken, userToken,
                    layer1,
                    idFromLayerEntity1,
                    relationships.get(layer1),
                    relationship != null && relationship.containsKey("object") ?
                            objectId :
                            idFromLayerEntity2
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return statusOperationRelationship;
    }

    private boolean addOrUpdateRelationshipIntoEntity(String sgeolInstance, String appToken, String userToken, String layer, String entityId, String relationshipName, Object objectIds) throws Exception {
        if (objectIds != null) {
            JSONObject payload = new JSONObject(buildRelationshipObject(objectIds));
            String uri = sgeolInstance + "/v2/" + layer + RELATIONSHIP + "?entity-id=" + entityId + "&relationship-name=" + relationshipName;
            HttpPost postRequest = new HttpPost(uri);
            LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put(APP_TOKEN, appToken);
            headers.put(USER_TOKEN, userToken);
            RequestsUtils requestsUtils = new RequestsUtils();
            requestsUtils.setHeadersParams(headers, postRequest);
            StringEntity entity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
            postRequest.setEntity(entity);
            // TODO: Avoid update relationship which already done.
            try {
                HttpResponse response = getHttpClientInstance().execute(postRequest);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                    HttpPut putRequest = new HttpPut(uri);
                    headers = new LinkedHashMap<>();
                    headers.put(APP_TOKEN, appToken);
                    headers.put(USER_TOKEN, userToken);
                    requestsUtils = new RequestsUtils();
                    requestsUtils.setHeadersParams(headers, putRequest);
                    putRequest.setEntity(entity);
                    response = getHttpClientInstance().execute(putRequest);
                }
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception();
            }
        }
        return false;
    }

    private Map<String, Object> buildRelationshipObject(Object objectIds) {
        Map<String, Object> relationship = new LinkedHashMap<>();
        relationship.put("type", "Relationship");
        relationship.put("object", objectIds);
        return relationship;
    }

    private boolean hasRelationshipObjectId(
            String sgeolInstance,
            String appToken,
            String userToken,
            String layer,
            String entityId,
            String relationshipName,
            String objectId,
            boolean objectIsString
    ) {
        String uri = sgeolInstance + "/v2/" + layer + FIND_ENTITY_BY_DOCUMENT;
        String query = "{\"$and\":[{\"_id\": \"" + entityId + "\"},{\"relationships." + relationshipName + ".object\":{\"$elemMatch\": {\"$eq\": \"" + objectId + "\"}}}]}";
        if (objectIsString)
            query = "{\"$and\":[{\"_id\": \"" + entityId + "\"},{\"relationships." + relationshipName + ".object\":{\"$eq\": \"" + objectId + "\"}}]}";

        HttpPost request = new HttpPost(uri);
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
                if (jsonArray == null || jsonArray.size() == 0)
                    return false;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> getEntityRelationshipsObjectIds(String sgeolInstance, String appToken, String userToken, String layer, String entityId, String relationshipName) {
        String uri = sgeolInstance + "/v2/" + layer + RELATIONSHIP + "?entity-id=" + entityId + "&relationship-name=" + relationshipName;
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
                Map<String, Object> relationships = mapper.readValue(
                        readBodyReq(response.getEntity().getContent()), Map.class
                );
                if (relationships.size() == 0 || relationships == null)
                    return null;
                return relationships;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> addObjectIdIntoRelationship(Object relationshipObject, String objectId) {
        if (relationshipObject instanceof String) {
            if (objectId.equals(relationshipObject))
                return null;
            else {
                List<String> object = new ArrayList<>();
                object.add((String) relationshipObject);
                object.add(objectId);
                return object;
            }
        } else if (relationshipObject instanceof Collection) {
            if (!((ArrayList<String>) relationshipObject).contains(objectId))
                ((ArrayList<String>) relationshipObject).add(objectId);
            return ((ArrayList<String>) relationshipObject);
        }
        return null;

    }
}

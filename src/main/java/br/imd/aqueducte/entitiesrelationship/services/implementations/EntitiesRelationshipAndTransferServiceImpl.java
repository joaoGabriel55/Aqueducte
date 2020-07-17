package br.imd.aqueducte.entitiesrelationship.services.implementations;

import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipAndTransferService;
import br.imd.aqueducte.entitiesrelationship.models.dtos.PropertyNGSILD;
import br.imd.aqueducte.entitiesrelationship.models.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.services.ImportNGSILDDataService;
import br.imd.aqueducte.services.sgeolqueriesservices.EntityOperationsService;
import br.imd.aqueducte.services.sgeolqueriesservices.RelationshipOperationsService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static br.imd.aqueducte.utils.FormatterUtils.treatPrimaryField;

@SuppressWarnings("ALL")
@Service
@Log4j2
public class EntitiesRelationshipAndTransferServiceImpl implements EntitiesRelationshipAndTransferService {

    public static final int STATUS_RELATIONSHIP_OK = 1;
    public static final int STATUS_RELATIONSHIP_ERROR = 2;
    public static final int STATUS_RELATIONSHIP_NOTHING_TODO = 3;

    public static final int STATUS_TRANSFER_OK = 4;
    public static final int STATUS_TRANSFER_ERROR = 5;
    public static final int STATUS_TRANSFER_NOTHING_TODO = 6;

    private static final int REQUEST_ENTITIES_LIMIT = 1024;

    private EntityOperationsService entityOperationsService = EntityOperationsService.getInstance();
    private RelationshipOperationsService relationshipOperationsService = RelationshipOperationsService.getInstance();

    @Autowired
    private ImportNGSILDDataService importNGSILDDataService;

    @Override
    public int relationshipManyToMany(EntitiesRelationshipSetup setup, String sgeolInstance, String appToken, String userToken) {
        List<Map<String, Object>> entities = new ArrayList<>();
        int offset = 0;
        int statusOperation = STATUS_RELATIONSHIP_NOTHING_TODO;
        String typeProperties = setup.getPropertiesLinked().get(0).getType();
        try {
            String layer1 = setup.getLayerSetup().get(0).getPath();
            String layer2 = setup.getLayerSetup().get(1).getPath();
            String property1 = treatPrimaryField(setup.getPropertiesLinked().get(0).getName());
            String property2 = treatPrimaryField(setup.getPropertiesLinked().get(1).getName());

            int index = 0;
            while (offset == 0 || entities.size() != 0) {
                int entitiesSize = entities.size();
                entities = getEntities(sgeolInstance, appToken, userToken, layer1, offset * entitiesSize);
                if (offset == 0 && (entities == null || entities.size() == 0)) {
                    return STATUS_RELATIONSHIP_NOTHING_TODO;
                }
                for (Object entity : entities) {
                    Map<String, Object> entityMap = (Map<String, Object>) entity;
                    log.info("[" + index + "] Entity ID: {}", entityMap.get("id"));
                    Object linkProperty1Value = entityMap.containsKey(property1) ?
                            getValue((Map<String, Object>) entityMap.get(property1)) :
                            null;
                    if (linkProperty1Value != null || typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                        List<String> response = new ArrayList<>();
                        if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                            if (entityMap.containsKey(property1)) {
                                response = entityOperationsService.findByDocument(
                                        sgeolInstance,
                                        layer2,
                                        property2,
                                        linkProperty1Value,
                                        setup.getPropertiesLinked().get(1).isTemporaryProperty(),
                                        appToken,
                                        userToken
                                );

                                statusOperation = relationshipAndDeleteTempProperties(
                                        sgeolInstance,
                                        appToken, userToken,
                                        statusOperation,
                                        response,
                                        layer1, layer2,
                                        entityMap.get("id").toString(),
                                        setup,
                                        property1, property2
                                );
                            }
                        } else if (typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                            int offset2 = 0;
                            List<String> geoResponse = new ArrayList<>();
                            while (offset2 == 0 || geoResponse.size() != 0) {
                                int geoResponseSize = geoResponse.size();
                                geoResponse = entityOperationsService.findContainedIn(
                                        sgeolInstance,
                                        layer2, layer1,
                                        entityMap.get("id").toString(),
                                        REQUEST_ENTITIES_LIMIT,
                                        offset2 * geoResponseSize,
                                        appToken,
                                        userToken
                                );
                                if (geoResponse == null || geoResponse.size() == 0) {
                                    break;
                                }
                                statusOperation = relationshipAndDeleteTempProperties(
                                        sgeolInstance,
                                        appToken, userToken,
                                        statusOperation,
                                        geoResponse,
                                        layer1, layer2,
                                        entityMap.get("id").toString(),
                                        setup,
                                        property1, property2
                                );
                                offset2++;
                            }
                        }
                    }
                    index++;
                }
                offset++;
            }
            return statusOperation;
        } catch (Exception e) {
            e.printStackTrace();
            return STATUS_RELATIONSHIP_ERROR;
        }
    }

    @Override
    public int relationshipOneToMany(EntitiesRelationshipSetup setup, String sgeolInstance, String appToken, String userToken) {
        int statusOperation = STATUS_RELATIONSHIP_NOTHING_TODO;
        String typeProperties = setup.getPropertiesLinked().get(0).getType();
        try {
            String layer1 = setup.getLayerSetup().get(0).getPath();
            String layer2 = setup.getLayerSetup().get(1).getPath();
            String property1 = treatPrimaryField(setup.getPropertiesLinked().get(0).getName());
            String property2 = treatPrimaryField(setup.getPropertiesLinked().get(1).getName());

            Map<String, Object> entity = getEntityById(
                    sgeolInstance, appToken, userToken, layer1, setup.getPropertiesLinked().get(0).getEntityId()
            );
            if (entity != null) {
                Object linkProperty1Value = entity.containsKey(property1) ?
                        getValue((Map<String, Object>) entity.get(property1)) :
                        null;
                if (linkProperty1Value != null || typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                    if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                        if (entity.containsKey(property1)) {
                            List<String> response = entityOperationsService.findByDocument(
                                    sgeolInstance,
                                    layer2,
                                    property2,
                                    linkProperty1Value,
                                    setup.getPropertiesLinked().get(1).isTemporaryProperty(),
                                    appToken,
                                    userToken
                            );

                            statusOperation = relationshipAndDeleteTempProperties(
                                    sgeolInstance,
                                    appToken, userToken,
                                    statusOperation,
                                    response,
                                    layer1, layer2,
                                    entity.get("id").toString(),
                                    setup,
                                    property1, property2
                            );
                        }
                    } else if (typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                        int offset2 = 0;
                        List<String> geoResponse = new ArrayList<>();
                        while (offset2 == 0 || geoResponse.size() != 0) {
                            int geoResponseSize = geoResponse.size();
                            geoResponse = entityOperationsService.findContainedIn(
                                    sgeolInstance,
                                    layer2, layer1,
                                    entity.get("id").toString(),
                                    REQUEST_ENTITIES_LIMIT,
                                    offset2 * geoResponseSize,
                                    appToken,
                                    userToken
                            );
                            if (geoResponse == null || geoResponse.size() == 0) {
                                break;
                            }
                            statusOperation = relationshipAndDeleteTempProperties(
                                    sgeolInstance,
                                    appToken, userToken,
                                    statusOperation,
                                    geoResponse,
                                    layer1, layer2,
                                    entity.get("id").toString(),
                                    setup,
                                    property1, property2
                            );
                            offset2++;
                        }
                    }
                }
            }
            return statusOperation;
        } catch (Exception e) {
            e.printStackTrace();
            return STATUS_RELATIONSHIP_ERROR;
        }
    }

    @Override
    public int relationshipOneToOne(EntitiesRelationshipSetup setup, String sgeolInstance, String appToken, String userToken) {
        int statusOperation = STATUS_RELATIONSHIP_NOTHING_TODO;
        String typeProperties = setup.getPropertiesLinked().get(0).getType();
        try {
            String layer1 = setup.getLayerSetup().get(0).getPath();
            String layer2 = setup.getLayerSetup().get(1).getPath();
            String property1 = treatPrimaryField(setup.getPropertiesLinked().get(0).getName());
            String property2 = treatPrimaryField(setup.getPropertiesLinked().get(1).getName());
            Map<String, Object> entity1 = getEntityById(
                    sgeolInstance, appToken, userToken, layer1, setup.getPropertiesLinked().get(0).getEntityId()
            );
            Map<String, Object> entity2 = getEntityById(
                    sgeolInstance, appToken, userToken, layer2, setup.getPropertiesLinked().get(1).getEntityId()
            );
            if (entity1 != null && entity2 != null) {
                if (entity1.containsKey(property1) && entity2.containsKey(property2)) {
                    boolean status = relationshipOperationsService.relationshipEntities(
                            sgeolInstance,
                            appToken, userToken,
                            layer1,
                            layer2,
                            entity1.get("id").toString(),
                            entity2.get("id").toString(),
                            setup.getRelationships()
                    );
                    if (status) statusOperation = STATUS_RELATIONSHIP_OK;
                    if (statusOperation == STATUS_RELATIONSHIP_OK) {
                        deleteTempProperties(
                                sgeolInstance,
                                appToken, userToken,
                                setup.getPropertiesLinked(),
                                layer1,
                                layer2,
                                entity1.get("id").toString(),
                                entity2.get("id").toString(),
                                property1,
                                property2
                        );
                    }
                }
            }
            return statusOperation;
        } catch (Exception e) {
            e.printStackTrace();
            return STATUS_RELATIONSHIP_ERROR;
        }
    }


    private int relationshipAndDeleteTempProperties(
            String sgeolInstance,
            String appToken,
            String userToken,
            int statusOperation,
            List<String> response,
            String layer1,
            String layer2,
            String idFromLayerEntity1,
            EntitiesRelationshipSetup setup,
            String property1,
            String property2
    ) throws Exception {
        for (String idFromLayerEntity2 : response) {
            boolean status = relationshipOperationsService.relationshipEntities(
                    sgeolInstance,
                    appToken, userToken,
                    layer1,
                    layer2,
                    idFromLayerEntity1,
                    idFromLayerEntity2,
                    setup.getRelationships()
            );
            if (status)
                statusOperation = STATUS_RELATIONSHIP_OK;

            if (statusOperation == STATUS_RELATIONSHIP_OK) {
                deleteTempProperties(
                        sgeolInstance,
                        appToken,
                        userToken,
                        setup.getPropertiesLinked(),
                        layer1,
                        layer2,
                        idFromLayerEntity1,
                        idFromLayerEntity2,
                        property1,
                        property2
                );
            }
        }
        return statusOperation;
    }


    private void deleteTempProperties(
            String sgeolInstance,
            String appToken,
            String userToken,
            List<PropertyNGSILD> properties,
            String layer1,
            String layer2,
            String entityIdLayer1,
            String entityIdLayer2,
            String property1,
            String property2
    ) throws Exception {
        if (properties.get(0).isTemporaryProperty()) {
            entityOperationsService.deleteEntityTempProperty(
                    sgeolInstance, appToken, userToken, layer1, entityIdLayer1, property1
            );
        }
        if (properties.get(1).isTemporaryProperty()) {
            entityOperationsService.deleteEntityTempProperty(
                    sgeolInstance, appToken, userToken, layer2, entityIdLayer2, property2);
        }
    }

    private Object getValue(Map<String, Object> property) {
        if (property.get("value") instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) property.get("value");
            if (map.containsKey("type") && map.get("type").equals("TempProperty")) {
                return map.get("value");
            }
            return null;
        } else {
            return property.get("value");
        }
    }

    private List<Map<String, Object>> getEntities(String sgeolInstance, String appToken, String userToken, String layer, int offset) throws Exception {
        return entityOperationsService.getEntitiesPageable(
                sgeolInstance, appToken, userToken, layer, REQUEST_ENTITIES_LIMIT, offset
        );
    }

    @Override
    @Async
    public CompletableFuture<Integer> transferLayerEntitiesAsync(String layer, String sgeolInstance, String appToken, String userToken) throws Exception {
        log.info("getLayerEntitiesAsync - {}", layer);
        final int status = transferLayerEntities(layer, sgeolInstance, appToken, userToken);
        return CompletableFuture.completedFuture(status);
    }

    @Override
    public int transferLayerEntities(String layer, String sgeolInstance, String appToken, String userToken) throws Exception {
        if (layer.contains("_preprocessing")) {
            boolean statusLayerEntitiesTransfered = false;
            boolean statusLayerDeleted = false;
            int status = 0;
            try {
                int offset = 0;
                String layerDestiny = layer.replace("_preprocessing", "");
                List<Map<String, Object>> entities = new ArrayList<>();
                while (offset == 0 || entities.size() != 0) {
                    entities = getEntities(sgeolInstance, appToken, userToken, layer, offset);
                    if (offset == 0 && (entities == null || entities.size() == 0)) {
                        return STATUS_TRANSFER_NOTHING_TODO;
                    }
                    JSONArray entitiesJsonArray = new JSONArray(entities);
                    statusLayerEntitiesTransfered = entityOperationsService.tranferPreprocessingLayerEntitesToFinalLayer(
                            sgeolInstance,
                            appToken, userToken,
                            layer, layerDestiny
                    );
                    offset++;
                }
                statusLayerDeleted = entityOperationsService.deleteDataFromPreprocessingLayer(
                        sgeolInstance, appToken, userToken, layer
                );
                if (statusLayerDeleted && statusLayerEntitiesTransfered)
                    return STATUS_TRANSFER_OK;
                else
                    return STATUS_TRANSFER_ERROR;
            } catch (Exception e) {
                e.printStackTrace();
                return STATUS_TRANSFER_ERROR;
            }
        }
        return STATUS_TRANSFER_NOTHING_TODO;
    }

    private Map<String, Object> getEntityById(String sgeolInstance, String appToken, String userToken, String layer, String id) {
        return entityOperationsService.findEntityById(sgeolInstance, appToken, userToken, layer, id);
    }
}

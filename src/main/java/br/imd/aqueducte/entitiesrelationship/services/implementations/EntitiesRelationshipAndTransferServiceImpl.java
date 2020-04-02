package br.imd.aqueducte.entitiesrelationship.services.implementations;

import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipAndTransferService;
import br.imd.aqueducte.services.sgeolqueriesservices.EntityOperationsService;
import br.imd.aqueducte.services.sgeolqueriesservices.RelationshipOperationsService;
import br.imd.aqueducte.models.entitiesrelationship.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.services.ImportNGSILDDataService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@SuppressWarnings("ALL")
@Service
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
    public int relationshipManyToMany(EntitiesRelationshipSetup setup) {
        List<Map<String, Object>> entities = new ArrayList<>();
        int offset = 0;
        int statusOperation = STATUS_RELATIONSHIP_NOTHING_TODO;
        String typeProperties = setup.getPropertiesLinked().get(0).getType();
        try {
            String layer1 = setup.getLayerSetup().get(0).getPath();
            String layer2 = setup.getLayerSetup().get(1).getPath();
            String property1 = setup.getPropertiesLinked().get(0).getName();
            String property2 = setup.getPropertiesLinked().get(1).getName();

            while (offset == 0 || entities.size() != 0) {
                entities = getEntities(layer1, offset);
                if (offset == 0 && (entities == null || entities.size() == 0)) {
                    return STATUS_RELATIONSHIP_NOTHING_TODO;
                }
                for (Object entity : entities) {
                    Map<String, Object> entityMap = (Map<String, Object>) entity;
                    if (entityMap.containsKey(property1)) {
                        Object linkProperty1Value = getValue((Map<String, Object>) entityMap.get(property1));
                        if (linkProperty1Value != null || typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                            List<String> response = new ArrayList<>();
                            if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                                response = entityOperationsService.findByDocument(
                                        layer2,
                                        property2,
                                        linkProperty1Value,
                                        setup.getPropertiesLinked().get(1).isTemporaryProperty(),
                                        "",
                                        "");

                                statusOperation = relationshipAndDeleteTempProperties(
                                        statusOperation,
                                        response,
                                        layer1, layer2,
                                        entityMap.get("id").toString(),
                                        setup,
                                        property1, property2
                                );
                            } else if (typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                                int offset2 = 0;
                                List<String> geoResponse = new ArrayList<>();
                                while (offset2 == 0 || geoResponse.size() != 0) {
                                    geoResponse = entityOperationsService.findContainedIn(
                                            layer2, layer1,
                                            entityMap.get("id").toString(),
                                            REQUEST_ENTITIES_LIMIT, offset2,
                                            "", ""
                                    );
                                    if (offset2 == 0 && (geoResponse == null || geoResponse.size() == 0)) {
                                        return STATUS_RELATIONSHIP_NOTHING_TODO;
                                    }
                                    statusOperation = relationshipAndDeleteTempProperties(
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
                    }
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
    public int relationshipOneToMany(EntitiesRelationshipSetup setup) {
        int statusOperation = STATUS_RELATIONSHIP_NOTHING_TODO;
        String typeProperties = setup.getPropertiesLinked().get(0).getType();
        try {
            String layer1 = setup.getLayerSetup().get(0).getPath();
            String layer2 = setup.getLayerSetup().get(1).getPath();
            String property1 = setup.getPropertiesLinked().get(0).getName();
            String property2 = setup.getPropertiesLinked().get(1).getName();

            Map<String, Object> entity = getEntityById(layer1, setup.getPropertiesLinked().get(0).getEntityId());
            if (entity != null) {
                if (entity.containsKey(property1)) {
                    Object linkProperty1Value = getValue((Map<String, Object>) entity.get(property1));
                    if (linkProperty1Value != null || typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                        if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                            List<String> response = entityOperationsService.findByDocument(
                                    layer2,
                                    property2,
                                    linkProperty1Value,
                                    setup.getPropertiesLinked().get(1).isTemporaryProperty(),
                                    "",
                                    "");

                            statusOperation = relationshipAndDeleteTempProperties(
                                    statusOperation,
                                    response,
                                    layer1, layer2,
                                    entity.get("id").toString(),
                                    setup,
                                    property1, property2
                            );
                        } else if (typeProperties.equals(PropertyNGSILD.GEOPROPERTY)) {
                            int offset2 = 0;
                            List<String> geoResponse = new ArrayList<>();
                            while (offset2 == 0 || geoResponse.size() != 0) {
                                geoResponse = entityOperationsService.findContainedIn(
                                        layer2, layer1,
                                        entity.get("id").toString(),
                                        REQUEST_ENTITIES_LIMIT, offset2,
                                        "", ""
                                );
                                if (offset2 == 0 && (geoResponse == null || geoResponse.size() == 0)) {
                                    return STATUS_RELATIONSHIP_NOTHING_TODO;
                                }
                                statusOperation = relationshipAndDeleteTempProperties(
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
            }
            return statusOperation;
        } catch (Exception e) {
            e.printStackTrace();
            return STATUS_RELATIONSHIP_ERROR;
        }
    }

    @Override
    public int relationshipOneToOne(EntitiesRelationshipSetup setup) {
        int statusOperation = STATUS_RELATIONSHIP_NOTHING_TODO;
        String typeProperties = setup.getPropertiesLinked().get(0).getType();
        try {
            String layer1 = setup.getLayerSetup().get(0).getPath();
            String layer2 = setup.getLayerSetup().get(1).getPath();
            String property1 = setup.getPropertiesLinked().get(0).getName();
            String property2 = setup.getPropertiesLinked().get(1).getName();
            Map<String, Object> entity1 = getEntityById(layer1, setup.getPropertiesLinked().get(0).getEntityId());
            Map<String, Object> entity2 = getEntityById(layer2, setup.getPropertiesLinked().get(1).getEntityId());
            if (entity1 != null && entity2 != null) {
                if (entity1.containsKey(property1) && entity2.containsKey(property2)) {
                    boolean status = relationshipOperationsService.relationshipEntities(
                            layer1,
                            layer2,
                            entity1.get("id").toString(),
                            entity2.get("id").toString(),
                            setup.getRelationships()
                    );
                    if (status) statusOperation = STATUS_RELATIONSHIP_OK;
                    if (statusOperation == STATUS_RELATIONSHIP_OK) {
                        deleteTempProperties(
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


    private int relationshipAndDeleteTempProperties(int statusOperation,
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
            List<PropertyNGSILD> properties,
            String layer1,
            String layer2,
            String entityIdLayer1,
            String entityIdLayer2,
            String property1,
            String property2
    ) throws Exception {
        if (properties.get(0).isTemporaryProperty()) {
            entityOperationsService.deleteEntityTempProperty(layer1, entityIdLayer1, property1);
        }
        if (properties.get(1).isTemporaryProperty()) {
            entityOperationsService.deleteEntityTempProperty(layer2, entityIdLayer2, property2);
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

    private List<Map<String, Object>> getEntities(String layer, int offset) throws Exception {
        return entityOperationsService.getEntitiesPageable(layer, REQUEST_ENTITIES_LIMIT, offset);
    }

    @Override
    @Async
    public CompletableFuture<Integer> transferLayerEntitiesAsync(String layer) throws Exception {
        logInfo("getLayerEntitiesAsync", null);
        final int status = transferLayerEntities(layer);
        return CompletableFuture.completedFuture(status);
    }

    public int transferLayerEntities(String layer) throws Exception {
        if (layer.contains("_preprocessing")) {
            boolean statusLayerEntitiesTransfered = false;
            boolean statusLayerDeleted = false;
            int status = 0;
            try {
                int offset = 0;
                String layerDestiny = layer.replace("_preprocessing", "");
                List<Map<String, Object>> entities = new ArrayList<>();
                while (offset == 0 || entities.size() != 0) {
                    entities = getEntities(layer, offset);
                    if (offset == 0 && (entities == null || entities.size() == 0)) {
                        return STATUS_TRANSFER_NOTHING_TODO;
                    }
                    JSONArray entitiesJsonArray = new JSONArray(entities);
                    statusLayerEntitiesTransfered = entityOperationsService.tranferPreprocessingLayerEntitesToFinalLayer(
                            layer, layerDestiny
                    );
                    offset++;
                }
                statusLayerDeleted = entityOperationsService.deleteDataFromPreprocessingLayer(layer);
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

    private Map<String, Object> getEntityById(String layer, String id) {
        return entityOperationsService.findEntityById(layer, id);
    }
}

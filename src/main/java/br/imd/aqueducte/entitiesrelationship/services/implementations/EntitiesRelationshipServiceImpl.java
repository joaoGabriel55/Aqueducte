package br.imd.aqueducte.entitiesrelationship.services.implementations;

import br.imd.aqueducte.entitiesrelationship.queriesservices.EntityOperationsService;
import br.imd.aqueducte.entitiesrelationship.queriesservices.RelationshipOperationsService;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipService;
import br.imd.aqueducte.models.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.mongodocuments.EntitiesRelationshipSetup;
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
public class EntitiesRelationshipServiceImpl implements EntitiesRelationshipService {

    public static final int STATUS_RELATIONSHIP_OK = 1;
    public static final int STATUS_RELATIONSHIP_ERROR = 2;
    public static final int STATUS_RELATIONSHIP_NOTHING_TODO = 3;

    public static final int STATUS_TRANSFER_OK = 4;
    public static final int STATUS_TRANSFER_ERROR = 5;
    public static final int STATUS_TRANSFER_NOTHING_TODO = 6;


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
            String layer1 = setup.getLayerNamePath().get(0).getPath();
            String layer2 = setup.getLayerNamePath().get(1).getPath();
            String property1 = setup.getPropertiesLinked().get(0).getName();
            String property2 = setup.getPropertiesLinked().get(1).getName();

            while (offset == 0 || entities.size() != 0) {
                entities = getEntities(layer1, offset);
                if (offset == 0 && (entities == null || entities.size() == 0)) {
                    return STATUS_RELATIONSHIP_NOTHING_TODO;
                }
                for (Object entity : entities) {
                    Map<String, Object> entityMap = (Map<String, Object>) entity;
                    if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                        if (entityMap.containsKey(property1)) {
                            Object linkProperty1Value = getValue((Map<String, Object>) entityMap.get(property1));
                            if (linkProperty1Value != null) {
                                List<String> response = entityOperationsService.findByDocument(
                                        layer2,
                                        property2,
                                        linkProperty1Value,
                                        setup.getPropertiesLinked().get(1).isTemporaryProperty(),
                                        "",
                                        "");
                                for (String idFromLayerEntity2 : response) {
                                    boolean status = relationshipOperationsService.relationshipEntities(
                                            layer1,
                                            layer2,
                                            entityMap.get("id").toString(),
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
                                                entityMap.get("id").toString(),
                                                idFromLayerEntity2,
                                                property1,
                                                property2
                                        );
                                    }
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
            String layer1 = setup.getLayerNamePath().get(0).getPath();
            String layer2 = setup.getLayerNamePath().get(1).getPath();
            String property1 = setup.getPropertiesLinked().get(0).getName();
            String property2 = setup.getPropertiesLinked().get(1).getName();

            Map<String, Object> entity = getEntityById(layer1, setup.getPropertiesLinked().get(0).getEntityId());
            if (entity != null) {
                if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                    if (entity.containsKey(property1)) {
                        Object linkProperty1Value = getValue((Map<String, Object>) entity.get(property1));
                        if (linkProperty1Value != null) {
                            List<String> response = entityOperationsService.findByDocument(
                                    layer2,
                                    property2,
                                    linkProperty1Value,
                                    setup.getPropertiesLinked().get(1).isTemporaryProperty(),
                                    "",
                                    "");
                            for (String idFromLayerEntity2 : response) {
                                boolean status = relationshipOperationsService.relationshipEntities(
                                        layer1,
                                        layer2,
                                        entity.get("id").toString(),
                                        idFromLayerEntity2,
                                        setup.getRelationships()
                                );
                                if (status) {
                                    statusOperation = STATUS_RELATIONSHIP_OK;
                                }

                                if (statusOperation == STATUS_RELATIONSHIP_OK) {
                                    deleteTempProperties(
                                            setup.getPropertiesLinked(),
                                            layer1,
                                            layer2,
                                            entity.get("id").toString(),
                                            idFromLayerEntity2,
                                            property1,
                                            property2
                                    );
                                }
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
            String layer1 = setup.getLayerNamePath().get(0).getPath();
            String layer2 = setup.getLayerNamePath().get(1).getPath();
            String property1 = setup.getPropertiesLinked().get(0).getName();
            String property2 = setup.getPropertiesLinked().get(1).getName();
            Map<String, Object> entity1 = getEntityById(layer1, setup.getPropertiesLinked().get(0).getEntityId());
            Map<String, Object> entity2 = getEntityById(layer2, setup.getPropertiesLinked().get(1).getEntityId());
            if (entity1 != null && entity2 != null) {
                if (typeProperties.equals(PropertyNGSILD.PROPERTY)) {
                    if (entity1.containsKey(property1) && entity2.containsKey(property2)) {
                        boolean status = relationshipOperationsService.relationshipEntities(
                                layer1,
                                layer2,
                                entity1.get("id").toString(),
                                entity2.get("id").toString(),
                                setup.getRelationships()
                        );
                        if (status) {
                            statusOperation = STATUS_RELATIONSHIP_OK;
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
        return entityOperationsService.getEntitiesPageable(layer, 1024, offset);
    }

    @Override
    @Async
    public CompletableFuture<Integer> transferLayerEntitiesAsync(String layer) throws Exception {
        logInfo("getLayerEntitiesAsync", null);
        final int status = transferLayerEntities(layer);
        return CompletableFuture.completedFuture(status);
    }

    private int transferLayerEntities(String layer) throws Exception {
        if (layer.contains("_preprocessing")) {
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
                    List<String> entitiesIds = importNGSILDDataService.importData(
                            "aluno_test1",
                            "",
                            "",
                            entitiesJsonArray
                    );
                    offset++;
                }
                entityOperationsService.deleteDataFromPreprocessingLayer(layer);
                return STATUS_TRANSFER_OK;
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

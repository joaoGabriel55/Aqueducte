package br.imd.aqueducte.entitiesrelationship.services.sgeol_middleware_services;

import java.util.Map;

public interface RelationshipOperationsService {

    boolean relationshipEntities(
            String sgeolInstance,
            String appToken,
            String userToken,
            String layer1,
            String layer2,
            String idFromLayerEntity1,
            String idFromLayerEntity2,
            Map<String, String> relationships
    ) throws Exception;

}

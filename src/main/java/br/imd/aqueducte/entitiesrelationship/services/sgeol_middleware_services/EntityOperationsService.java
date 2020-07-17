package br.imd.aqueducte.entitiesrelationship.services.sgeol_middleware_services;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface EntityOperationsService {

    List<Map<String, Object>> getEntitiesPageable(
            String sgeolInstance, String appToken, String userToken, String layer, int limit, int offset
    ) throws Exception;

    Map<String, Object> findEntityById(String sgeolInstance, String appToken, String userToken, String layer, String id);

    List<String> findByDocument(String sgeolInstance,
                                String layer,
                                String propertyName,
                                Object value,
                                boolean isTempProperty,
                                String appToken,
                                String userToken
    );

    boolean updateEntity(
            String sgeolInstance, String id, String appToken, String userToken,
            LinkedHashMap<String, Object> entity, String layer
    );

    List<String> findContainedIn(String sgeolInstance,
                                 String layer,
                                 String containerLayer,
                                 String containerEntityId,
                                 int limit,
                                 int offset,
                                 String appToken,
                                 String userToken
    );

    boolean transferPreprocessingLayerEntitiesToFinalLayer(
            String sgeolInstance, String appToken, String userToken, String preprocessingLayer, String finalLayer
    ) throws IOException;

    boolean deleteEntityTempProperty(
            String sgeolInstance, String appToken, String userToken, String layer, String entityId, String propertyName
    ) throws Exception;

    boolean deleteDataFromPreprocessingLayer(
            String sgeolInstance, String appToken, String userToken, String preprocessingLayer
    ) throws Exception;
}

package br.imd.aqueducte.entitiesrelationship.services;

import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public interface EntitiesRelationshipAndTransferService {

    int relationshipManyToMany(EntitiesRelationshipSetup setup, String sgeolInstance, String appToken, String userToken);

    int relationshipOneToMany(EntitiesRelationshipSetup setup, String sgeolInstance, String appToken, String userToken);

    int relationshipOneToOne(EntitiesRelationshipSetup setup, String sgeolInstance, String appToken, String userToken);

    CompletableFuture<Integer> transferLayerEntitiesAsync(String layer, String sgeolInstance, String appToken, String userToken) throws Exception;

    int transferLayerEntities(String layer, String sgeolInstance, String appToken, String userToken) throws Exception;

}

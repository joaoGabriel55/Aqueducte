package br.imd.aqueducte.entitiesrelationship.services;

import br.imd.aqueducte.models.mongodocuments.EntitiesRelationshipSetup;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public interface EntitiesRelationshipService {

    int relationshipManyToMany(EntitiesRelationshipSetup setup);

    int relationshipOneToMany(EntitiesRelationshipSetup setup);

    int relationshipOneToOne(EntitiesRelationshipSetup setup);

    CompletableFuture<Integer> transferLayerEntitiesAsync(String layer) throws Exception;

}

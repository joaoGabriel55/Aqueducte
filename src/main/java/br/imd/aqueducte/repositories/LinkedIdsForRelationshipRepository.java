package br.imd.aqueducte.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.imd.aqueducte.models.LinkedIdsForRelationship;

public interface LinkedIdsForRelationshipRepository extends MongoRepository<LinkedIdsForRelationship, String> {

//    @Query(value = "{ _id : ?0}")
    List<LinkedIdsForRelationship> findByIdImportationSetupWithoutContext(String id);

}

package br.imd.smartsysnc.repositories;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.models.LinkedIdsForRelationship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LinkedIdsForRelationshipRepository extends MongoRepository<LinkedIdsForRelationship, String> {

//    @Query(value = "{ _id : ?0}")
    List<LinkedIdsForRelationship> findByIdImportationSetupWithoutContext(String id);

}

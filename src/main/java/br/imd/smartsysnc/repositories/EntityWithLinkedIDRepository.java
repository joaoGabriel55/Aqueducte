package br.imd.smartsysnc.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.imd.smartsysnc.models.EntityWithLinkedID;

public interface EntityWithLinkedIDRepository extends MongoRepository<EntityWithLinkedID, String> {

}

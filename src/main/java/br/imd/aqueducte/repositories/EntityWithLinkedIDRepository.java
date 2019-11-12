package br.imd.aqueducte.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.imd.aqueducte.models.documents.EntityWithLinkedID;

public interface EntityWithLinkedIDRepository extends MongoRepository<EntityWithLinkedID, String> {

	EntityWithLinkedID findByEntitySGEOL(String entitySGEOL);

}

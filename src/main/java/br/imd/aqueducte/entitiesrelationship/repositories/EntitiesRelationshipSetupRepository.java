package br.imd.aqueducte.entitiesrelationship.repositories;

import br.imd.aqueducte.entitiesrelationship.models.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.repositories.GenericRepository;

import java.util.List;

public interface EntitiesRelationshipSetupRepository extends GenericRepository<EntitiesRelationshipSetup> {

    List<EntitiesRelationshipSetup> findByStatus(String status);

}

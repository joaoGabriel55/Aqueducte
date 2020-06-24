package br.imd.aqueducte.entitiesrelationship.services;

import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.services.GenericService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EntitiesRelationshipSetupService extends GenericService<EntitiesRelationshipSetup> {

    List<EntitiesRelationshipSetup> findByStatus(String status) throws Exception;

}

package br.imd.aqueducte.entitiesrelationship.services.implementations;

import br.imd.aqueducte.entitiesrelationship.repositories.EntitiesRelationshipSetupRepository;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipSetupService;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.services.implementations.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntitiesRelationshipSetupServiceImpl
        extends GenericServiceImpl<EntitiesRelationshipSetup>
        implements EntitiesRelationshipSetupService {

    @Autowired
    private EntitiesRelationshipSetupRepository repository;

//    @Override
//    public List<EntitiesRelationshipSetup> findByStatus(String status) {
//        return this.repository.findByStatus(status);
//    }
}

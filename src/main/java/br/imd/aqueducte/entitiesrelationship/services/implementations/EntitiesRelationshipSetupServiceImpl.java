package br.imd.aqueducte.entitiesrelationship.services.implementations;

import br.imd.aqueducte.entitiesrelationship.repositories.EntitiesRelationshipSetupRepository;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipSetupService;
import br.imd.aqueducte.entitiesrelationship.models.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.services.implementations.GenericServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class EntitiesRelationshipSetupServiceImpl
        extends GenericServiceImpl<EntitiesRelationshipSetup>
        implements EntitiesRelationshipSetupService {

    @Autowired
    private EntitiesRelationshipSetupRepository repository;

    @Override
    public List<EntitiesRelationshipSetup> findByStatus(String status) throws Exception {
        try {
            log.error("EntitiesRelationshipSetupService findByStatus - {}", status);
            return this.repository.findByStatus(status);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }
}

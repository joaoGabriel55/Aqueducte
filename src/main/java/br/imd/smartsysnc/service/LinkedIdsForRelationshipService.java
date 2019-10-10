package br.imd.smartsysnc.service;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.models.LinkedIdsForRelationship;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LinkedIdsForRelationshipService extends GenericService<LinkedIdsForRelationship> {

    List<LinkedIdsForRelationship> saveWithoutMapListIdLinkedLinkedIdsForRelationship(
            List<String> fieldsSelectedForRelationship,
            String idImportationSetupContext);

    List<LinkedIdsForRelationship> createBatch(List<LinkedIdsForRelationship> fieldsSelectedForRelationship);

    List<LinkedIdsForRelationship> findByIdImportationSetup(String idImportationSetupWithoutContext);
}

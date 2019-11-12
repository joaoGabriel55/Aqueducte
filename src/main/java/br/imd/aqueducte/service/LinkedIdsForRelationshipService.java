package br.imd.aqueducte.service;

import java.util.List;

import org.springframework.stereotype.Component;

import br.imd.aqueducte.models.documents.LinkedIdsForRelationship;

@Component
public interface LinkedIdsForRelationshipService extends GenericService<LinkedIdsForRelationship> {

    List<LinkedIdsForRelationship> saveWithoutMapListIdLinkedLinkedIdsForRelationship(
            List<String> fieldsSelectedForRelationship,
            String idImportationSetupContext);

    List<LinkedIdsForRelationship> createBatch(List<LinkedIdsForRelationship> fieldsSelectedForRelationship);

    List<LinkedIdsForRelationship> findByIdImportationSetup(String idImportationSetupWithoutContext);
}

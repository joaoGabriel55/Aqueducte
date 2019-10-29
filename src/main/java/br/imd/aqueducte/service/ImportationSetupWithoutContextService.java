package br.imd.aqueducte.service;

import br.imd.aqueducte.models.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportationSetupWithoutContextService extends GenericService<ImportationSetupWithoutContext> {
    ImportationSetupWithoutContext treatCreateImportationWithoutContextSetup(
    		String userId,	
            ImportationSetupWithoutContext importationSetupWithoutContext,
            List<String> fieldsSelectedForRelationship,
            boolean isUpdate
    );

    //TODO Improve that
    Page<ImportationSetupWithoutContext> findAllLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(int page, int count);
}

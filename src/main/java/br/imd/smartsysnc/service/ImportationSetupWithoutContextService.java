package br.imd.smartsysnc.service;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportationSetupWithoutContextService extends GenericService<ImportationSetupWithoutContext> {
    ImportationSetupWithoutContext treatCreateImportationWithoutContextSetup(
            ImportationSetupWithoutContext importationSetupWithoutContext,
            List<String> fieldsSelectedForRelationship,
            boolean isUpdate
    );

    //TODO Improve that
    Page<ImportationSetupWithoutContext> findAllLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(int page, int count);
}

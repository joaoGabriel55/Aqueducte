package br.imd.aqueducte.service;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface ImportationSetupWithoutContextService extends GenericService<ImportationSetupWithoutContext> {
    Page<ImportationSetupWithoutContext> findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String importType, int page, int count
    );
}

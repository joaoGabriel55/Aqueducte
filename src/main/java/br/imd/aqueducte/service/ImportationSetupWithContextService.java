package br.imd.aqueducte.service;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface ImportationSetupWithContextService extends GenericService<ImportationSetupWithContext> {
    Page<ImportationSetupWithContext> findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String importType, int page, int count
    );
}

package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportationSetupWithoutContextService extends GenericService<ImportationSetupWithoutContext> {
    Page<ImportationSetupWithoutContext> findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String importType, int page, int count
    );

    List<ImportationSetupWithoutContext> findByUserIdAndFilePath(String userId, String filePath);
}

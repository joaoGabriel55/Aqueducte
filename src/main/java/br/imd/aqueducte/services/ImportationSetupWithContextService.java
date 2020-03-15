package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportationSetupWithContextService extends GenericService<ImportationSetupWithContext> {
    Page<ImportationSetupWithContext> findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String importType, int page, int count
    );

    List<ImportationSetupWithContext> findByUserIdAndFilePath(String userId, String filePath);

}

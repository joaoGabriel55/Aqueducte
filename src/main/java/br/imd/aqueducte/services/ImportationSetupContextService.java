package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportationSetupContextService extends GenericService<ImportationSetupContext> {
    Page<ImportationSetupContext> findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String idUser, String importType, int page, int count
    ) throws Exception;

    List<ImportationSetupContext> findByUserIdAndFilePath(String userId, String filePath) throws Exception;

}

package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupStandard;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportationSetupStandardService extends GenericService<ImportationSetupStandard> {
    Page<ImportationSetupStandard> findByIdUserImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String idUser, String importType, int page, int count
    ) throws Exception;

    List<ImportationSetupStandard> findByUserIdAndFilePath(String userId, String filePath) throws Exception;
}

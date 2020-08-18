package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImportNGSILDDataSetupService extends GenericService<ImportNGSILDDataSetup> {

    ImportNGSILDDataSetup createOrUpdate(String userId, ImportNGSILDDataSetup setup) throws Exception;

    Page<ImportNGSILDDataSetup> findByIdUserAndImportTypeAndUseContextOrderByDateCreatedDesc(
            String idUser, String importType, boolean useContext, int page, int count
    ) throws Exception;

    List<ImportNGSILDDataSetup> findByUserIdAndFilePath(String userId, String filePath) throws Exception;
}

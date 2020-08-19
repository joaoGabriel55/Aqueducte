package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImportNGSILDDataSetupRepository extends MongoRepository<ImportNGSILDDataSetup, String> {

    Page<ImportNGSILDDataSetup> findByIdUserAndImportTypeAndUseContextOrderByDateCreatedDesc(
            String idUser, String importType, boolean useContext, Pageable pages
    );

    Page<ImportNGSILDDataSetup> findByIdUserOrderByDateCreatedDesc(String idUser, Pageable pages);

    List<ImportNGSILDDataSetup> findByIdUserAndFilePath(String userId, String filePath);
}

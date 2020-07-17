package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupStandard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImportationSetupStandardRepository extends MongoRepository<ImportationSetupStandard, String> {

    Page<ImportationSetupStandard> findByIdUserAndImportTypeOrderByDateCreatedDesc(
            String idUser, String importType, Pageable pages
    );

    List<ImportationSetupStandard> findByIdUserAndFilePath(String userId, String filePath);
}

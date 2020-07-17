package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImportationSetupContextRepository extends MongoRepository<ImportationSetupContext, String> {

    Page<ImportationSetupContext> findByIdUserAndImportTypeOrderByDateCreatedDesc(
            String idUser, String importType, Pageable pages
    );

    List<ImportationSetupContext> findByIdUserAndFilePath(String userId, String filePath);
}

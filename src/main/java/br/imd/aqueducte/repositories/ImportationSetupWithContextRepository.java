package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImportationSetupWithContextRepository extends MongoRepository<ImportationSetupWithContext, String> {

    Page<ImportationSetupWithContext> findByIdUserAndImportTypeOrderByDateCreatedDesc(
            String idUser, String importType, Pageable pages
    );

    List<ImportationSetupWithContext> findByIdUserAndFilePath(String userId, String filePath);
}

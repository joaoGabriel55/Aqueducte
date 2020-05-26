package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImportationSetupWithoutContextRepository extends MongoRepository<ImportationSetupWithoutContext, String> {

    Page<ImportationSetupWithoutContext> findByIdUserAndImportTypeOrderByDateCreatedDesc(
            String idUser, String importType, Pageable pages
    );

    List<ImportationSetupWithoutContext> findByIdUserAndFilePath(String userId, String filePath);
}

package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.documents.ImportationSetupWithContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportationSetupWithContextRepository extends MongoRepository<ImportationSetupWithContext, String> {
    Page<ImportationSetupWithContext> findAllByOrderByDateCreatedDesc(Pageable pages);
}
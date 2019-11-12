package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.documents.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportationSetupWithoutContextRepository extends MongoRepository<ImportationSetupWithoutContext, String> {

    Page<ImportationSetupWithoutContext> findAllByOrderByDateCreatedDesc(Pageable pages);

}

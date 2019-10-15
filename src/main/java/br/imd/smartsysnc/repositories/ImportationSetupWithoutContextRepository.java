package br.imd.smartsysnc.repositories;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImportationSetupWithoutContextRepository extends MongoRepository<ImportationSetupWithoutContext, String> {

    Page<ImportationSetupWithoutContext> findAllByOrderByDateCreatedDesc(Pageable pages);

}

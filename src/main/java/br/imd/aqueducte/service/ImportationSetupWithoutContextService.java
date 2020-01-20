package br.imd.aqueducte.service;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface ImportationSetupWithoutContextService extends GenericService<ImportationSetupWithoutContext> {

    //TODO Improve that
    Page<ImportationSetupWithoutContext> findAllLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(int page, int count);
}

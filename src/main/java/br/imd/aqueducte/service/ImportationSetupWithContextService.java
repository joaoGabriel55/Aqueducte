package br.imd.aqueducte.service;

import br.imd.aqueducte.models.documents.ImportationSetupWithContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface ImportationSetupWithContextService extends GenericService<ImportationSetupWithContext> {
    Page<ImportationSetupWithContext> findAllPageable(int page, int count);

}

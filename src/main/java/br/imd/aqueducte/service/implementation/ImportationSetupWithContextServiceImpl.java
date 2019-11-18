package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.documents.ImportationSetupWithContext;
import br.imd.aqueducte.repositories.ImportationSetupWithContextRepository;
import br.imd.aqueducte.service.ImportationSetupWithContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImportationSetupWithContextServiceImpl implements ImportationSetupWithContextService {

    @Autowired
    private ImportationSetupWithContextRepository importationSetupWithContextRepository;

    @Override
    public ImportationSetupWithContext createOrUpdate(ImportationSetupWithContext obj) {
        return this.importationSetupWithContextRepository.save(obj);
    }

    @Override
    public List<ImportationSetupWithContext> findAll() {
        return null;
    }

    @Override
    public Optional<ImportationSetupWithContext> findById(String id) {
        return this.importationSetupWithContextRepository.findById(id);
    }

    @Override
    public Page<ImportationSetupWithContext> findAllPageable(int page, int count) {

        PageRequest pageable = new PageRequest(page, count);
        return this.importationSetupWithContextRepository.findAllByOrderByDateCreatedDesc(pageable);
    }

    @Override
    public String delete(String id) {
        Optional<ImportationSetupWithContext> impSetupWithCxt = findById(id);
        if (!impSetupWithCxt.isPresent())
            return null;
        String idForDelete = impSetupWithCxt.get().getId();
        importationSetupWithContextRepository.deleteById(idForDelete);
        return idForDelete;
    }
}
package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.repositories.ImportationSetupWithContextRepository;
import br.imd.aqueducte.services.ImportationSetupWithContextService;
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
    public Page<ImportationSetupWithContext> findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String importType,
            int page,
            int count
    ) {
        PageRequest pageable = new PageRequest(page, count);
        return this.importationSetupWithContextRepository.findByImportTypeOrderByDateCreatedDesc(importType, pageable);
    }

    @Override
    public List<ImportationSetupWithContext> findByUserIdAndFilePath(String userId, String filePath) {
        return this.importationSetupWithContextRepository.findByIdUserAndFilePath(userId, filePath);
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

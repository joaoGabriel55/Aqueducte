package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.repositories.ImportationSetupWithContextRepository;
import br.imd.aqueducte.services.ImportationSetupWithContextService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ImportationSetupWithContextServiceImpl implements ImportationSetupWithContextService {

    @Autowired
    private ImportationSetupWithContextRepository importationSetupWithContextRepository;

    @Override
    public ImportationSetupWithContext createOrUpdate(ImportationSetupWithContext obj) throws Exception {
        try {
            log.info("createOrUpdate ImportationSetupWithContext");
            return this.importationSetupWithContextRepository.save(obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupWithContext> findAll() {
        return null;
    }

    @Override
    public Optional<ImportationSetupWithContext> findById(String id) throws Exception {
        try {
            log.info("findById ImportationSetupWithContext - {}", id);
            return this.importationSetupWithContextRepository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<ImportationSetupWithContext> findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String idUser,
            String importType,
            int page,
            int count
    ) throws Exception {
        try {
            PageRequest pageable = new PageRequest(page, count);
            log.info("findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated ImportationSetupWithContext - page: {} count: {}", page, count);
            return this.importationSetupWithContextRepository.findByIdUserAndImportTypeOrderByDateCreatedDesc(
                    idUser, importType, pageable
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupWithContext> findByUserIdAndFilePath(String userId, String filePath) throws Exception {
        try {
            log.info("findByUserIdAndFilePath ImportationSetupWithContext");
            return this.importationSetupWithContextRepository.findByIdUserAndFilePath(userId, filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public String delete(String id) throws Exception {
        try {
            Optional<ImportationSetupWithContext> impSetupWithCxt = findById(id);
            if (!impSetupWithCxt.isPresent()) {
                log.error("ImportationSetupWithContext not found - {}", id);
                throw new Exception();
            }
            String idForDelete = impSetupWithCxt.get().getId();
            importationSetupWithContextRepository.deleteById(idForDelete);
            log.info("delete ImportationSetupWithContext - {}", id);
            return idForDelete;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }
}

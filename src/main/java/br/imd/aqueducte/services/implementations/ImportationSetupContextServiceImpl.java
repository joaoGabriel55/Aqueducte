package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupContext;
import br.imd.aqueducte.repositories.ImportationSetupContextRepository;
import br.imd.aqueducte.services.ImportationSetupContextService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ImportationSetupContextServiceImpl implements ImportationSetupContextService {

    @Autowired
    private ImportationSetupContextRepository importationSetupContextRepository;

    @Override
    public ImportationSetupContext createOrUpdate(ImportationSetupContext obj) throws Exception {
        try {
            log.info("createOrUpdate ImportationSetupWithContext");
            return this.importationSetupContextRepository.save(obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupContext> findAll() {
        return null;
    }

    @Override
    public Optional<ImportationSetupContext> findById(String id) throws Exception {
        try {
            log.info("findById ImportationSetupWithContext - {}", id);
            return this.importationSetupContextRepository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<ImportationSetupContext> findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String idUser,
            String importType,
            int page,
            int count
    ) throws Exception {
        try {
            PageRequest pageable = new PageRequest(page, count);
            log.info("findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated ImportationSetupWithContext - page: {} count: {}", page, count);
            return this.importationSetupContextRepository.findByIdUserAndImportTypeOrderByDateCreatedDesc(
                    idUser, importType, pageable
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupContext> findByUserIdAndFilePath(String userId, String filePath) throws Exception {
        try {
            log.info("findByUserIdAndFilePath ImportationSetupWithContext");
            return this.importationSetupContextRepository.findByIdUserAndFilePath(userId, filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public String delete(String id) throws Exception {
        try {
            Optional<ImportationSetupContext> impSetupWithCxt = findById(id);
            if (!impSetupWithCxt.isPresent()) {
                log.error("ImportationSetupWithContext not found - {}", id);
                throw new Exception();
            }
            String idForDelete = impSetupWithCxt.get().getId();
            importationSetupContextRepository.deleteById(idForDelete);
            log.info("delete ImportationSetupWithContext - {}", id);
            return idForDelete;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }
}

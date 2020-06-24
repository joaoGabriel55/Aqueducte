package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.repositories.ImportationSetupWithoutContextRepository;
import br.imd.aqueducte.services.ImportationSetupWithoutContextService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ImportationSetupWithoutContextServiceImpl implements ImportationSetupWithoutContextService {

    @Autowired
    private ImportationSetupWithoutContextRepository importationSetupWithoutContextRepository;

    @SuppressWarnings("deprecation")
    @Override
    public Page<ImportationSetupWithoutContext> findByIdUserImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String idUser,
            String importType,
            int page,
            int count
    ) throws Exception {
        try {
            PageRequest pageable = new PageRequest(page, count);
            log.info("findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated ImportationSetupWithoutContext - page: {} count: {}", page, count);
            return this.importationSetupWithoutContextRepository.findByIdUserAndImportTypeOrderByDateCreatedDesc(
                    idUser, importType, pageable
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupWithoutContext> findByUserIdAndFilePath(String userId, String filePath) throws Exception {
        try {
            log.info("findByUserIdAndFilePath ImportationSetupWithoutContext");
            return this.importationSetupWithoutContextRepository.findByIdUserAndFilePath(userId, filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public ImportationSetupWithoutContext createOrUpdate(
            ImportationSetupWithoutContext importationSetupWithoutContext) throws Exception {
        try {
            log.info("createOrUpdate ImportationSetupWithoutContext");
            return this.importationSetupWithoutContextRepository.save(importationSetupWithoutContext);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupWithoutContext> findAll() throws Exception {
        try {
            log.info("findAll ImportationSetupWithoutContext");
            return this.importationSetupWithoutContextRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Optional<ImportationSetupWithoutContext> findById(String id) throws Exception {
        try {
            log.info("findById ImportationSetupWithoutContext - {}", id);
            return this.importationSetupWithoutContextRepository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public String delete(String id) throws Exception {
        try {
            Optional<ImportationSetupWithoutContext> impSetupWithoutCxt = findById(id);
            if (impSetupWithoutCxt.isEmpty()) {
                log.error("ImportationSetupWithoutContext not found - {}", id);
                throw new Exception();
            }
            String idForDelete = impSetupWithoutCxt.get().getId();
            importationSetupWithoutContextRepository.deleteById(idForDelete);
            log.info("delete ImportationSetupWithoutContext - {}", id);
            return idForDelete;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

}

package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupStandard;
import br.imd.aqueducte.repositories.ImportationSetupStandardRepository;
import br.imd.aqueducte.services.ImportationSetupStandardService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ImportationSetupStandardServiceImpl implements ImportationSetupStandardService {

    @Autowired
    private ImportationSetupStandardRepository importationSetupStandardRepository;

    @SuppressWarnings("deprecation")
    @Override
    public Page<ImportationSetupStandard> findByIdUserImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            String idUser,
            String importType,
            int page,
            int count
    ) throws Exception {
        try {
            PageRequest pageable = new PageRequest(page, count);
            log.info("findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated ImportationSetupWithoutContext - page: {} count: {}", page, count);
            return this.importationSetupStandardRepository.findByIdUserAndImportTypeOrderByDateCreatedDesc(
                    idUser, importType, pageable
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupStandard> findByUserIdAndFilePath(String userId, String filePath) throws Exception {
        try {
            log.info("findByUserIdAndFilePath ImportationSetupWithoutContext");
            return this.importationSetupStandardRepository.findByIdUserAndFilePath(userId, filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public ImportationSetupStandard createOrUpdate(
            ImportationSetupStandard importationSetupStandard) throws Exception {
        try {
            log.info("createOrUpdate ImportationSetupWithoutContext");
            return this.importationSetupStandardRepository.save(importationSetupStandard);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportationSetupStandard> findAll() throws Exception {
        try {
            log.info("findAll ImportationSetupWithoutContext");
            return this.importationSetupStandardRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Optional<ImportationSetupStandard> findById(String id) throws Exception {
        try {
            log.info("findById ImportationSetupWithoutContext - {}", id);
            return this.importationSetupStandardRepository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public String delete(String id) throws Exception {
        try {
            Optional<ImportationSetupStandard> impSetupWithoutCxt = findById(id);
            if (impSetupWithoutCxt.isEmpty()) {
                log.error("ImportationSetupWithoutContext not found - {}", id);
                throw new Exception();
            }
            String idForDelete = impSetupWithoutCxt.get().getId();
            importationSetupStandardRepository.deleteById(idForDelete);
            log.info("delete ImportationSetupWithoutContext - {}", id);
            return idForDelete;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

}

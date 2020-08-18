package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import br.imd.aqueducte.repositories.ImportNGSILDDataSetupRepository;
import br.imd.aqueducte.services.ImportNGSILDDataSetupService;
import br.imd.aqueducte.services.validators.ImportNGSILDDataSetupValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ImportNGSILDDataSetupServiceImpl implements ImportNGSILDDataSetupService {
    @Autowired
    private ImportNGSILDDataSetupRepository repository;

    @Autowired
    private ImportNGSILDDataSetupValidator validator;

    @Override
    public ImportNGSILDDataSetup createOrUpdate(String userId, ImportNGSILDDataSetup setup) throws Exception {
        if (setup.getIdUser() == null || setup.getIdUser() == "")
            setup.setIdUser(userId);
        validator.validImportNGSILDDataSetup(setup);
        return this.createOrUpdate(setup);
    }

    @Override
    public ImportNGSILDDataSetup createOrUpdate(ImportNGSILDDataSetup setup) throws Exception {
        try {
            if (setup.getId() == null)
                setup.setDateCreated(new Date());
            else
                setup.setDateCreated(setup.getDateCreated());

            setup.setDateModified(new Date());
            log.info("createOrUpdate ImportNGSILDDataSetup");
            return this.repository.save(setup);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportNGSILDDataSetup> findAll() {
        return null;
    }

    @Override
    public Optional<ImportNGSILDDataSetup> findById(String id) throws Exception {
        try {
            log.info("findById ImportNGSILDDataSetup - {}", id);
            return this.repository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<ImportNGSILDDataSetup> findByIdUserAndImportTypeAndUseContextOrderByDateCreatedDesc(
            String idUser,
            String importType,
            boolean useContext,
            int page,
            int count
    ) throws Exception {
        try {
            PageRequest pageable = new PageRequest(page, count);
            log.info("findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated ImportNGSILDDataSetup - page: {} count: {}", page, count);
            return this.repository.findByIdUserAndImportTypeAndUseContextOrderByDateCreatedDesc(
                    idUser, importType, useContext, pageable
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<ImportNGSILDDataSetup> findByUserIdAndFilePath(String userId, String filePath) throws Exception {
        try {
            log.info("findByUserIdAndFilePath ImportNGSILDDataSetup");
            return this.repository.findByIdUserAndFilePath(userId, filePath);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public String delete(String id) throws Exception {
        try {
            Optional<ImportNGSILDDataSetup> impSetupWithCxt = findById(id);
            if (!impSetupWithCxt.isPresent()) {
                log.error("ImportNGSILDDataSetup not found - {}", id);
                throw new Exception();
            }
            String idForDelete = impSetupWithCxt.get().getId();
            repository.deleteById(idForDelete);
            log.info("delete ImportNGSILDDataSetup - {}", id);
            return idForDelete;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }
}

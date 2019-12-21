package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.documents.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.documents.LinkedIdsForRelationship;
import br.imd.aqueducte.repositories.ImportationSetupWithoutContextRepository;
import br.imd.aqueducte.service.ImportationSetupWithoutContextService;
import br.imd.aqueducte.service.LinkedIdsForRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ImportationSetupWithoutContextServiceImpl implements ImportationSetupWithoutContextService {

    @Autowired
    private ImportationSetupWithoutContextRepository importationSetupWithoutContextRepository;

    @Autowired
    private LinkedIdsForRelationshipService linkedIdsForRelationshipService;

    @Override
    public ImportationSetupWithoutContext treatCreateImportationWithoutContextSetup(String userId,
                                                                                    ImportationSetupWithoutContext importationSetupWithoutContext, List<String> fieldsSelectedForRelationship,
                                                                                    boolean isUpdate) {

        if (fieldsSelectedForRelationship != null) {
            List<LinkedIdsForRelationship> linkedIdsForRelationshipList = linkedIdsForRelationshipService
                    .saveWithoutMapListIdLinkedLinkedIdsForRelationship(fieldsSelectedForRelationship,
                            importationSetupWithoutContext.getId());
            importationSetupWithoutContext.setLinkedIdsForRelationshipList(linkedIdsForRelationshipList);
        }

        if (importationSetupWithoutContext.getFieldsGeolocationSelected().size() > 0)
            importationSetupWithoutContext.setSelectedGeolocationData(true);
        if (importationSetupWithoutContext.getBodyData() != null)
            importationSetupWithoutContext.setUseBodyData(true);

        if (importationSetupWithoutContext.getIdUser() == null)
            importationSetupWithoutContext.setIdUser(userId);

        if (!isUpdate) {
            importationSetupWithoutContext.setDateCreated(new Date());
        }
        importationSetupWithoutContext.setDateModified(new Date());
        return importationSetupWithoutContext;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Page<ImportationSetupWithoutContext> findAllLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
            int page, int count) {
        PageRequest pageable = new PageRequest(page, count);
        return this.importationSetupWithoutContextRepository.findAllByOrderByDateCreatedDesc(pageable);
    }

    @Override
    public ImportationSetupWithoutContext createOrUpdate(
            ImportationSetupWithoutContext importationSetupWithoutContext) {
        return this.importationSetupWithoutContextRepository.save(importationSetupWithoutContext);
    }

    @Override
    public List<ImportationSetupWithoutContext> findAll() {
        return this.importationSetupWithoutContextRepository.findAll();
    }

    @Override
    public Optional<ImportationSetupWithoutContext> findById(String id) {
        return this.importationSetupWithoutContextRepository.findById(id);
    }

    @Override
    public String delete(String id) {
        Optional<ImportationSetupWithoutContext> impSetupWithoutCxt = findById(id);
        if (!impSetupWithoutCxt.isPresent())
            return null;
        String idForDelete = impSetupWithoutCxt.get().getId();
        importationSetupWithoutContextRepository.deleteById(idForDelete);
        return idForDelete;

    }

}

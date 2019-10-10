package br.imd.smartsysnc.service.implementation;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.models.LinkedIdsForRelationship;
import br.imd.smartsysnc.repositories.ImportationSetupWithoutContextRepository;
import br.imd.smartsysnc.service.ImportationSetupWithoutContextService;
import br.imd.smartsysnc.service.LinkedIdsForRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ImportationSetupWithoutContext treatCreateImportationWithoutContextSetup(
            ImportationSetupWithoutContext importationSetupWithoutContext,
            List<String> fieldsSelectedForRelationship) {

        if (fieldsSelectedForRelationship != null) {
            List<LinkedIdsForRelationship> linkedIdsForRelationshipList = linkedIdsForRelationshipService
                    .saveWithoutMapListIdLinkedLinkedIdsForRelationship(
                            fieldsSelectedForRelationship,
                            importationSetupWithoutContext.getId());
            importationSetupWithoutContext.setLinkedIdsForRelationshipList(linkedIdsForRelationshipList);
        }

        if (importationSetupWithoutContext.getFieldsGeolocationSelected().size() > 0)
            importationSetupWithoutContext.setSelectedGeolocationData(true);

        importationSetupWithoutContext.setDateCreated(new Date());
        importationSetupWithoutContext.setDateModified(new Date());
        return importationSetupWithoutContext;
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

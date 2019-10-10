package br.imd.smartsysnc.service.implementation;

import br.imd.smartsysnc.models.LinkedIdsForRelationship;
import br.imd.smartsysnc.repositories.LinkedIdsForRelationshipRepository;
import br.imd.smartsysnc.service.LinkedIdsForRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LinkedIdsForRelationshipServiceImpl implements LinkedIdsForRelationshipService {

    @Autowired
    private LinkedIdsForRelationshipRepository linkedIdsForRelationshipRepository;

    @Override
    public List<LinkedIdsForRelationship> saveWithoutMapListIdLinkedLinkedIdsForRelationship(
            List<String> fieldsSelectedForRelationship,
            String idImportationSetupContext) {
        List<LinkedIdsForRelationship> linkedIdsForRelationshipList = new ArrayList<>();
        for (String field : fieldsSelectedForRelationship) {
            LinkedIdsForRelationship linkedIdsForRelationship = new LinkedIdsForRelationship();
            linkedIdsForRelationship.setIdImportationSetupWithoutContext(idImportationSetupContext);
            linkedIdsForRelationship.setFieldUsedForRelationship(field);
            linkedIdsForRelationshipList.add(linkedIdsForRelationship);
        }

        return this.createBatch(linkedIdsForRelationshipList);
    }

    @Override
    public List<LinkedIdsForRelationship> createBatch(List<LinkedIdsForRelationship> fieldsSelectedForRelationship) {
        return linkedIdsForRelationshipRepository.insert(fieldsSelectedForRelationship);
    }

    @Override
    public List<LinkedIdsForRelationship> findByIdImportationSetup(String idImportationSetupWithoutContext) {
        return this.linkedIdsForRelationshipRepository.findByIdImportationSetupWithoutContext(idImportationSetupWithoutContext);
    }

    @Override
    public LinkedIdsForRelationship createOrUpdate(LinkedIdsForRelationship obj) {
        return linkedIdsForRelationshipRepository.save(obj);
    }

    @Override
    public List<LinkedIdsForRelationship> findAll() {
        return null;
    }

    @Override
    public Optional<LinkedIdsForRelationship> findById(String id) {
        return Optional.empty();
    }

    @Override
    public String delete(String id) {
        return null;
    }
}

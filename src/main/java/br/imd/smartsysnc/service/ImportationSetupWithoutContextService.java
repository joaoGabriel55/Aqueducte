package br.imd.smartsysnc.service;

import org.springframework.stereotype.Component;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;

import java.util.List;

@Component
public interface ImportationSetupWithoutContextService extends GenericService<ImportationSetupWithoutContext> {
    ImportationSetupWithoutContext treatCreateImportationWithoutContextSetup(ImportationSetupWithoutContext importationSetupWithoutContext, List<String> fieldsSelectedForRelationship);
}

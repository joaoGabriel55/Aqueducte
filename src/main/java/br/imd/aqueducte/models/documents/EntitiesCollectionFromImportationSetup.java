package br.imd.aqueducte.models.documents;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document
public class EntitiesCollectionFromImportationSetup {
    @Id
    private String id;

    @Lazy
    private List<Map<ImportationSetupWithContext, List<Map<String, Object>>>> entitiesCollectionsFromImportationSetups;
}

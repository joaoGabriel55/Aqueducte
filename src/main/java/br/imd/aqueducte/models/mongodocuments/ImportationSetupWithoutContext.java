package br.imd.aqueducte.models.mongodocuments;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ImportationSetupWithoutContext extends ImportationSetup {
    public ImportationSetupWithoutContext() {
        super();
    }
}

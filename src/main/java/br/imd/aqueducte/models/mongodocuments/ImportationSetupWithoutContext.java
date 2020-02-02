package br.imd.aqueducte.models.mongodocuments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportationSetupWithoutContext extends ImportationSetup {
    public ImportationSetupWithoutContext() {
        super();
    }
}

package br.imd.aqueducte.models.documents;

import br.imd.aqueducte.models.ImportationSetup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportationSetupWithoutContext extends ImportationSetup {

    public ImportationSetupWithoutContext() {
        super();
    }

    @DBRef(lazy = true)
    private List<LinkedIdsForRelationship> linkedIdsForRelationshipList;

    public List<LinkedIdsForRelationship> getLinkedIdsForRelationshipList() {
        return linkedIdsForRelationshipList;
    }

    public void setLinkedIdsForRelationshipList(List<LinkedIdsForRelationship> linkedIdsForRelationshipList) {
        this.linkedIdsForRelationshipList = linkedIdsForRelationshipList;
    }
}

package br.imd.aqueducte.models.entitiesrelationship.mongodocuments;

import br.imd.aqueducte.models.entitiesrelationship.dtos.LayerSetup;
import br.imd.aqueducte.models.entitiesrelationship.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.enums.RelationshipType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.models.config.MongoDBCollectionsConfig.ENTITIES_RELATIONSHIP_SETUP;

@Document(value = ENTITIES_RELATIONSHIP_SETUP)
@Getter
@Setter
@NoArgsConstructor
public class EntitiesRelationshipSetup {

    @Id
    private String id;

    private String idUser;

    @NotBlank
    private List<LayerSetup> layerSetup;

    /**
     * Layer path and PropertyNGSILD
     */
    @NotBlank
    private List<PropertyNGSILD> propertiesLinked;

    /**
     * ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY
     */
    private RelationshipType relationshipType;

    /**
     * Layer path and relationship name
     */
    @NotBlank
    private Map<String, String> relationships;

    @NotBlank
    private EntitiesRelationshipSetupStatus status;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedDate
    private Date dateModified;

}

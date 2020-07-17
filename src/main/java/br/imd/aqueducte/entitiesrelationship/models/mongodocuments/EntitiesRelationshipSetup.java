package br.imd.aqueducte.entitiesrelationship.models.mongodocuments;

import br.imd.aqueducte.entitiesrelationship.models.dtos.LayerSetup;
import br.imd.aqueducte.entitiesrelationship.models.dtos.PropertyNGSILD;
import br.imd.aqueducte.entitiesrelationship.models.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.entitiesrelationship.models.enums.RelationshipType;
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

@Document
@Getter
@Setter
@NoArgsConstructor
public class EntitiesRelationshipSetup {

    @Id
    private String id;

    private String idUser;

    private boolean useContext;

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
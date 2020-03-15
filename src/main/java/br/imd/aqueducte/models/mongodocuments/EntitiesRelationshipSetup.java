package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.dtos.LayerNamePath;
import br.imd.aqueducte.models.dtos.PropertyNGSILD;
import br.imd.aqueducte.models.enums.RelationshipType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.models.mongodocuments.config.MongoDBCollectionsConfig.ENTITIES_RELATIONSHIP_SETUP;

@Document(value = ENTITIES_RELATIONSHIP_SETUP)
public class EntitiesRelationshipSetup {

    @Id
    private String id;

    private String idUser;

    @NotBlank
    private List<LayerNamePath> layerNamePath;

    /**
     * Layer path and PropertyNGSILD
     */
    @NotBlank
    private List<PropertyNGSILD> propertiesLinked;

    /**
     * ONE_TO_ONE, ONE_TO_MANY, MANY_TO_MANY
     */
    @NotBlank
    private RelationshipType relationshipType;

    /**
     * Layer path and relationship name
     */
    @NotBlank
    private Map<String, String> relationships;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedDate
    private Date dateModified;

    public EntitiesRelationshipSetup() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public List<LayerNamePath> getLayerNamePath() {
        return layerNamePath;
    }

    public void setLayerNamePath(List<LayerNamePath> layerNamePath) {
        this.layerNamePath = layerNamePath;
    }

    public List<PropertyNGSILD> getPropertiesLinked() {
        return propertiesLinked;
    }

    public void setPropertiesLinked(List<PropertyNGSILD> propertiesLinked) {
        this.propertiesLinked = propertiesLinked;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public Map<String, String> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, String> relationships) {
        this.relationships = relationships;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}

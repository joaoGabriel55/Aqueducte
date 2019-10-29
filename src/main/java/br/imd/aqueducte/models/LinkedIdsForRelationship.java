package br.imd.aqueducte.models;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class has the objective, link the id from API/File with urn:id generated for Smart Geo Layers (SGEOL)
 */
@Document
public class LinkedIdsForRelationship {

    @Id
    private String id;

    private String idImportationSetupWithoutContext;

    private String fieldUsedForRelationship;

    private List<Map<String, Object>> mapListIdLinked;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdImportationSetupWithoutContext() {
        return idImportationSetupWithoutContext;
    }

    public void setIdImportationSetupWithoutContext(String idImportationSetupWithoutContext) {
        this.idImportationSetupWithoutContext = idImportationSetupWithoutContext;
    }

    public String getFieldUsedForRelationship() {
        return fieldUsedForRelationship;
    }

    public void setFieldUsedForRelationship(String fieldUsedForRelationship) {
        this.fieldUsedForRelationship = fieldUsedForRelationship;
    }

    public List<Map<String, Object>> getMapListIdLinked() {
        return mapListIdLinked;
    }

    public void setMapListIdLinked(List<Map<String, Object>> mapListIdLinked) {
        this.mapListIdLinked = mapListIdLinked;
    }
}

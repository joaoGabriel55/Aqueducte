package br.imd.aqueducte.models.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("ALL")
public class MatchingConfig {

    private String contextName;

    private String foreignProperty;

    /**
     * Responsible for just link data between differents data sets <br/>
     * <b>Is transient<b/>
     */
    @JsonProperty
    private boolean isTransientField;

    @JsonProperty
    private boolean isPrimaryField;

    // TODO: isAccumulativeField?

    @JsonProperty
    private boolean isLocation;

    private List<GeoLocationConfig> geoLocationConfig;

    @JsonProperty
    private boolean hasRelationship;

    private RelationshipConfig relationshipConfig;

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getForeignProperty() {
        return foreignProperty;
    }

    public void setForeignProperty(String foreignProperty) {
        this.foreignProperty = foreignProperty;
    }

    public boolean isTransientField() {
        return isTransientField;
    }

    public boolean isPrimaryField() {
        return isPrimaryField;
    }

    public void setPrimaryField(boolean primaryField) {
        isPrimaryField = primaryField;
    }

    public void setTransientField(boolean transientField) {
        isTransientField = transientField;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public List<GeoLocationConfig> getGeoLocationConfig() {
        return geoLocationConfig;
    }

    public void setGeoLocationConfig(List<GeoLocationConfig> geoLocationConfig) {
        this.geoLocationConfig = geoLocationConfig;
    }

    public boolean isHasRelationship() {
        return hasRelationship;
    }

    public void setHasRelationship(boolean hasRelationship) {
        this.hasRelationship = hasRelationship;
    }

    public RelationshipConfig getRelationshipConfig() {
        return relationshipConfig;
    }

    public void setRelationshipConfig(RelationshipConfig relationshipConfig) {
        this.relationshipConfig = relationshipConfig;
    }

    public MatchingConfig fromLinkedHashMap(LinkedHashMap<String, Object> linkedHashMap) {
        this.contextName = (String) linkedHashMap.get("contextName");
        this.foreignProperty = (String) linkedHashMap.get("foreignProperty");
        this.isLocation = (boolean) linkedHashMap.get("isLocation");
        this.geoLocationConfig = (List<GeoLocationConfig>) linkedHashMap.get("geoLocationConfig");
        this.hasRelationship = (boolean) linkedHashMap.get("hasRelationship");
        this.relationshipConfig = (RelationshipConfig) linkedHashMap.get("relationshipConfig");
        this.isTransientField = (boolean) linkedHashMap.get("isTransientField");
        return this;
    }
}

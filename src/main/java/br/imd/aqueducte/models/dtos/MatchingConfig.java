package br.imd.aqueducte.models.dtos;

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
    private boolean isTemporaryField;

    private boolean isLocation;

    private List<GeoLocationConfig> geoLocationConfig;

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

    public boolean isTemporaryField() {
        return isTemporaryField;
    }

    public void setTemporaryField(boolean temporaryField) {
        isTemporaryField = temporaryField;
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

    public MatchingConfig fromLinkedHashMap(LinkedHashMap<String, Object> linkedHashMap) {
        this.contextName = (String) linkedHashMap.get("contextName");
        this.foreignProperty = (String) linkedHashMap.get("foreignProperty");
        this.isLocation = (boolean) linkedHashMap.get("isLocation");
        this.geoLocationConfig = (List<GeoLocationConfig>) linkedHashMap.get("geoLocationConfig");
        this.isTemporaryField = (boolean) linkedHashMap.get("temporaryField");
        return this;
    }
}
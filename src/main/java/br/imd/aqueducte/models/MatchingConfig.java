package br.imd.aqueducte.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MatchingConfig {

    private String contextName;

    private String foreignProperty;

    @JsonProperty
    private boolean isLocation;

    private List<LocationGeoJsonConfig> locationToGeoJsonConfig;

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

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public List<LocationGeoJsonConfig> getLocationToGeoJsonConfig() {
        return locationToGeoJsonConfig;
    }

    public void setLocationToGeoJsonConfig(List<LocationGeoJsonConfig> locationToGeoJsonConfig) {
        this.locationToGeoJsonConfig = locationToGeoJsonConfig;
    }
}

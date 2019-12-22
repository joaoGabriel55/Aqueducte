package br.imd.aqueducte.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.stream.Collectors;

public class MatchingConfig {

    private String contextName;

    private String foreignProperty;

    @JsonProperty
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

    public LinkedHashMap<String, Object> toLinkedHashMap() {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("contextName", this.contextName);
        linkedHashMap.put("foreignProperty", this.foreignProperty);
        linkedHashMap.put("isLocation", this.isLocation);
        List<Map<String, Object>> geoLocationConfigToMapList = geoLocationConfig.stream().map(elem ->
                elem.toLinkedHashMap()).collect(Collectors.toList());
        linkedHashMap.put("geoLocationConfig", geoLocationConfigToMapList);
        return linkedHashMap;
    }
}

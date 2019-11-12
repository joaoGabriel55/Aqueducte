package br.imd.aqueducte.models;

import java.util.Map;

public class MatchingConfig {

    private String contextName;
    private String foreignProperty;
    private boolean isLocation;
    private Map<Object, Object> fieldsGeolocationSelectedConfigs;

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

    public Map<Object, Object> getFieldsGeolocationSelectedConfigs() {
        return fieldsGeolocationSelectedConfigs;
    }

    public void setFieldsGeolocationSelectedConfigs(Map<Object, Object> fieldsGeolocationSelectedConfigs) {
        this.fieldsGeolocationSelectedConfigs = fieldsGeolocationSelectedConfigs;
    }
}

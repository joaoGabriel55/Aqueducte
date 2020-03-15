package br.imd.aqueducte.models.dtos;

public class PropertyNGSILD {
    public static final String GEOPROPERTY = "GeoProperty";
    public static final String PROPERTY = "Property";

    private String entityId;
    private String name;
    private String type;
    private boolean temporaryProperty;

    public PropertyNGSILD() {
    }

    public PropertyNGSILD(String name, String type, boolean temporaryProperty) {
        this.name = name;
        this.type = type;
        this.temporaryProperty = temporaryProperty;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTemporaryProperty() {
        return temporaryProperty;
    }

    public void setTemporaryProperty(boolean temporaryProperty) {
        this.temporaryProperty = temporaryProperty;
    }
}

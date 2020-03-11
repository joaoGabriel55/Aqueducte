package br.imd.aqueducte.models.dtos;

public class RelationshipConfig {

    private String type;
    private String identityField;
    private String fieldFrom;
    private String contextNameParent;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentityField() {
        return identityField;
    }

    public void setIdentityField(String identityField) {
        this.identityField = identityField;
    }

    public String getFieldFrom() {
        return fieldFrom;
    }

    public void setFieldFrom(String fieldFrom) {
        this.fieldFrom = fieldFrom;
    }

    public String getContextNameParent() {
        return contextNameParent;
    }

    public void setContextNameParent(String contextNameParent) {
        this.contextNameParent = contextNameParent;
    }
}

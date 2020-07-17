package br.imd.aqueducte.entitiesrelationship.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PropertyNGSILD {
    public static final String GEOPROPERTY = "GeoProperty";
    public static final String PROPERTY = "Property";

    private String entityId;
    private String name;
    private String type;
    private boolean temporaryProperty;

    public PropertyNGSILD(String name, String type, boolean temporaryProperty) {
        this.name = name;
        this.type = type;
        this.temporaryProperty = temporaryProperty;
    }
}

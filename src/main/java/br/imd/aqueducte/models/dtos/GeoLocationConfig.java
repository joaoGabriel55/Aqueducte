package br.imd.aqueducte.models.dtos;

import java.util.Map;

/**
JSON structure:
{
    "key": "localizacao",
    "typeOfSelection": "string",
    "invertCoords": true,
    "delimiter": ",",
    "typeGeolocation": "Point"
}
* */
public class GeoLocationConfig {

    private String key;
    private String typeOfSelection;
    private boolean invertCoords;
    private String delimiter;
    private String typeGeolocation;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTypeOfSelection() {
        return typeOfSelection;
    }

    public void setTypeOfSelection(String typeOfSelection) {
        this.typeOfSelection = typeOfSelection;
    }

    public boolean isInvertCoords() {
        return invertCoords;
    }

    public void setInvertCoords(boolean invertCoords) {
        this.invertCoords = invertCoords;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getTypeGeolocation() {
        return typeGeolocation;
    }

    public void setTypeGeolocation(String typeGeolocation) {
        this.typeGeolocation = typeGeolocation;
    }

    public GeoLocationConfig fromLinkedHashMap(Map<String, Object> linkedHashMap) {
        this.key = (String) linkedHashMap.get("key");
        this.typeOfSelection = (String) linkedHashMap.get("typeOfSelection");
        this.invertCoords = (boolean) linkedHashMap.get("invertCoords");
        this.delimiter = (String) linkedHashMap.get("delimiter");
        this.typeGeolocation = (String) linkedHashMap.get("typeGeolocation");
        return this;
    }
}

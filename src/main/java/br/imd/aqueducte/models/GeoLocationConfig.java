package br.imd.aqueducte.models;

import java.util.LinkedHashMap;

/*
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

    public LinkedHashMap<String, Object> toLinkedHashMap() {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("key", this.key);
        linkedHashMap.put("typeOfSelection", this.typeOfSelection);
        linkedHashMap.put("invertCoords", this.invertCoords);
        linkedHashMap.put("delimiter", this.delimiter);
        linkedHashMap.put("typeGeolocation", this.typeGeolocation);
        return linkedHashMap;
    }
}

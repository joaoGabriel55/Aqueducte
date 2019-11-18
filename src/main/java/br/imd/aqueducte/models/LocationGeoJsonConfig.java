package br.imd.aqueducte.models;

public class LocationGeoJsonConfig {

    private String key;
    private String typeOfSelection;
    private String typeGeolocation;
    private String delimiter;
    private String singleFieldLocation;

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

    public String getTypeGeolocation() {
        return typeGeolocation;
    }

    public void setTypeGeolocation(String typeGeolocation) {
        this.typeGeolocation = typeGeolocation;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getSingleFieldLocation() {
        return singleFieldLocation;
    }

    public void setSingleFieldLocation(String singleFieldLocation) {
        this.singleFieldLocation = singleFieldLocation;
    }
}

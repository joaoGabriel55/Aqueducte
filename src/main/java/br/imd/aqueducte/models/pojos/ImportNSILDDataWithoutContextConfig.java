package br.imd.aqueducte.models.pojos;

import java.util.LinkedHashMap;
import java.util.List;

public class ImportNSILDDataWithoutContextConfig {

    private List<GeoLocationConfig> geoLocationConfig;
    private List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion;

    public List<GeoLocationConfig> getGeoLocationConfig() {
        return geoLocationConfig;
    }

    public void setGeoLocationConfig(List<GeoLocationConfig> geoLocationConfig) {
        this.geoLocationConfig = geoLocationConfig;
    }

    public List<LinkedHashMap<String, Object>> getDataContentForNGSILDConversion() {
        return dataContentForNGSILDConversion;
    }

    public void setDataContentForNGSILDConversion(List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion) {
        this.dataContentForNGSILDConversion = dataContentForNGSILDConversion;
    }
}

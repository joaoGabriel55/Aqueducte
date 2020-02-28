package br.imd.aqueducte.models.dtos;

import java.util.List;
import java.util.Map;

public class ImportNSILDDataWithoutContextConfig {

    private List<GeoLocationConfig> geoLocationConfig;
    private List<Map<String, Object>> dataContentForNGSILDConversion;

    public List<GeoLocationConfig> getGeoLocationConfig() {
        return geoLocationConfig;
    }

    public void setGeoLocationConfig(List<GeoLocationConfig> geoLocationConfig) {
        this.geoLocationConfig = geoLocationConfig;
    }

    public List<Map<String, Object>> getDataContentForNGSILDConversion() {
        return dataContentForNGSILDConversion;
    }

    public void setDataContentForNGSILDConversion(List<Map<String, Object>> dataContentForNGSILDConversion) {
        this.dataContentForNGSILDConversion = dataContentForNGSILDConversion;
    }
}

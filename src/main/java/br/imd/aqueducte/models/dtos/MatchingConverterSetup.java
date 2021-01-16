package br.imd.aqueducte.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;

@Getter
@Setter
@NoArgsConstructor
public class MatchingConverterSetup {

    private String finalProperty;
    private String foreignProperty;
    private boolean isLocation;
    private LinkedHashMap<String, GeoLocationConfig> geoLocationConfig;
    private boolean isDate;
    private LinkedHashMap<String, String> datePropertyConfig;
}

package br.imd.aqueducte.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatchingConfig {

    private String contextName;

    private String foreignProperty;

    /**
     * Responsible for just link data between different data sets <br/>
     * <b>Is transient<b/>
     */
    private boolean isTemporaryField;

    private boolean isLocation;

    private List<GeoLocationConfig> geoLocationConfig;

}
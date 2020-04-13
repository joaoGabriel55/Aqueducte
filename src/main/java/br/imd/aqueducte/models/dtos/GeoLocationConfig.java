package br.imd.aqueducte.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON structure:
 * {
 * "key": "localizacao",
 * "typeOfSelection": "string",
 * "invertCoords": true,
 * "delimiter": ",",
 * "typeGeolocation": "Point"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class GeoLocationConfig {

    private String key;
    private String typeOfSelection;
    private boolean invertCoords;
    private String delimiter;
    private String typeGeolocation;

}

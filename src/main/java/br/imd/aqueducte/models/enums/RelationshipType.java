package br.imd.aqueducte.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RelationshipType {
    @JsonProperty("ONE_TO_ONE")
    ONE_TO_ONE,
    @JsonProperty("ONE_TO_MANY")
    ONE_TO_MANY,
    @JsonProperty("MANY_TO_MANY")
    MANY_TO_MANY
}

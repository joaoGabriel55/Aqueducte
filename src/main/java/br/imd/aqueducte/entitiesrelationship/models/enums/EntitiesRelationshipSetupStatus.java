package br.imd.aqueducte.entitiesrelationship.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EntitiesRelationshipSetupStatus {

    @JsonProperty("DONE")
    DONE,
    @JsonProperty("PENDING")
    PENDING
}

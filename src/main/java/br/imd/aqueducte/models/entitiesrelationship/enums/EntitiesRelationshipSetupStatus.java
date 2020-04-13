package br.imd.aqueducte.models.entitiesrelationship.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EntitiesRelationshipSetupStatus {

    @JsonProperty("DONE")
    DONE,
    @JsonProperty("PENDING")
    PENDING
}

package br.imd.aqueducte.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskStatus {
    @JsonProperty("DONE")
    DONE,
    @JsonProperty("PROCESSING")
    PROCESSING,
    @JsonProperty("ERROR")
    ERROR
}

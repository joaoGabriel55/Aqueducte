package br.imd.aqueducte.models.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskType {
    @JsonProperty("IMPORT_DATA")
    IMPORT_DATA,
    @JsonProperty("UPLOAD_FILE")
    UPLOAD_FILE,
    @JsonProperty("RELATIONSHIP_ENTITIES")
    RELATIONSHIP_ENTITIES
}

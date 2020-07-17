package br.imd.aqueducte.entitiesrelationship.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class LayerSetup {
    private String name;
    private String path;
    private boolean isPreprocessingLayer;
    private List<Map<String, String>> contextSources;
    private String importSetupId;
}
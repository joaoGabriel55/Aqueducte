package br.imd.aqueducte.models.entitiesrelationship.dtos;

import java.util.List;

public class LayerSetup {
    private String name;
    private String path;
    private boolean isPreprocessingLayer;
    private List<String> contextSources;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isPreprocessingLayer() {
        return isPreprocessingLayer;
    }

    public void setPreprocessingLayer(boolean preprocessingLayer) {
        isPreprocessingLayer = preprocessingLayer;
    }

    public List<String> getContextSources() {
        return contextSources;
    }

    public void setContextSources(List<String> contextSources) {
        this.contextSources = contextSources;
    }
}
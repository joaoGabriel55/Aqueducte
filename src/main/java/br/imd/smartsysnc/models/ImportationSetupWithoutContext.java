package br.imd.smartsysnc.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportationSetupWithoutContext {

    @Id
    private String id;

    private String label;

    private String description;

    private String baseUrl;

    private String path;

    private String httpVerb;

    private boolean isUseBodyData;

    private Map<Object, Object> bodyData;

    private Map<Object, Object> queryParameters;

    private Map<Object, Object> headersParameters;

    private String dataSelected;

    private String layerSelected;

    private List<String> fieldsAvailable;

    private List<String> fieldsSelected;


    private boolean isSelectedGeolocationData;

    private List<String> fieldsGeolocationSelected;

    private List<Map<Object, Object>> fieldsGeolocationSelectedConfigs;

    @DBRef(lazy = true)
    private List<LinkedIdsForRelationship> linkedIdsForRelationshipList;

    private Date dateCreated;

    private Date dateModified;

    // ===================================================================================
    // Getters & Setters
    // ===================================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpVerb() {
        return httpVerb;
    }

    public void setHttpVerb(String httpVerb) {
        this.httpVerb = httpVerb;
    }

    public boolean isUseBodyData() {
        return isUseBodyData;
    }

    public void setUseBodyData(boolean useBodyData) {
        isUseBodyData = useBodyData;
    }

    public Map<Object, Object> getBodyData() {
        return bodyData;
    }

    public void setBodyData(Map<Object, Object> bodyData) {
        this.bodyData = bodyData;
    }

    public Map<Object, Object> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<Object, Object> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public Map<Object, Object> getHeadersParameters() {
        return headersParameters;
    }

    public void setHeadersParameters(Map<Object, Object> headersParameters) {
        this.headersParameters = headersParameters;
    }

    public String getDataSelected() {
        return dataSelected;
    }

    public void setDataSelected(String dataSelected) {
        this.dataSelected = dataSelected;
    }

    public String getLayerSelected() {
        return layerSelected;
    }

    public void setLayerSelected(String layerSelected) {
        this.layerSelected = layerSelected;
    }

    public List<String> getFieldsAvailable() {
        return fieldsAvailable;
    }

    public void setFieldsAvailable(List<String> fieldsAvailable) {
        this.fieldsAvailable = fieldsAvailable;
    }

    public List<String> getFieldsSelected() {
        return fieldsSelected;
    }

    public void setFieldsSelected(List<String> fieldsSelected) {
        this.fieldsSelected = fieldsSelected;
    }

    public boolean isSelectedGeolocationData() {
        return isSelectedGeolocationData;
    }

    public void setSelectedGeolocationData(boolean selectedGeolocationData) {
        isSelectedGeolocationData = selectedGeolocationData;
    }

    public List<String> getFieldsGeolocationSelected() {
        return fieldsGeolocationSelected;
    }

    public void setFieldsGeolocationSelected(List<String> fieldsGeolocationSelected) {
        this.fieldsGeolocationSelected = fieldsGeolocationSelected;
    }

    public List<Map<Object, Object>> getFieldsGeolocationSelectedConfigs() {
        return fieldsGeolocationSelectedConfigs;
    }

    public void setFieldsGeolocationSelectedConfigs(List<Map<Object, Object>> fieldsGeolocationSelectedConfigs) {
        this.fieldsGeolocationSelectedConfigs = fieldsGeolocationSelectedConfigs;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public List<LinkedIdsForRelationship> getLinkedIdsForRelationshipList() {
        return linkedIdsForRelationshipList;
    }

    public void setLinkedIdsForRelationshipList(List<LinkedIdsForRelationship> linkedIdsForRelationshipList) {
        this.linkedIdsForRelationshipList = linkedIdsForRelationshipList;
    }
}

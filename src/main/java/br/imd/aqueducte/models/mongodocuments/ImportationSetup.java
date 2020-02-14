package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.pojos.GeoLocationConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class ImportationSetup {

    @Id
    private String id;

    private String idUser;

    @Indexed(unique = true)
    @NotBlank(message = "Label required")
    private String label;

    /**
     * importType: FILE or WEBSERVICE
     */
    @NotBlank(message = "Import type required")
    private String importType;

    private String description;

    private String baseUrl;

    private String path;

    private String httpVerb;

    @JsonIgnore
    private boolean isUseBodyData;

    private Map<Object, Object> bodyData;

    private Map<Object, Object> queryParameters;

    private Map<Object, Object> headersParameters;

    private String dataSelected;

    /**
     * File path from HDFS Storage (Aqueconnect microservice)
     */
    private String filePath;

    /**
     * Delimiter for file content stored at HDFS (Aqueconnect microservice)
     */
    private String delimiterFileContent;

    @NotBlank(message = "Layer required")
    private String layerSelected;

    @NotBlank(message = "Layer path required")
    private String layerPathSelected;

    private List<String> fieldsAvailable;

    private List<String> fieldsSelected;

    @JsonIgnore
    private boolean isSelectedGeolocationData;

    private List<String> fieldsGeolocationSelected;

    private List<GeoLocationConfig> fieldsGeolocationSelectedConfigs;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedDate
    private Date dateModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDelimiterFileContent() {
        return delimiterFileContent;
    }

    public void setDelimiterFileContent(String delimiterFileContent) {
        this.delimiterFileContent = delimiterFileContent;
    }

    public String getLayerSelected() {
        return layerSelected;
    }

    public void setLayerSelected(String layerSelected) {
        this.layerSelected = layerSelected;
    }

    public String getLayerPathSelected() {
        return layerPathSelected;
    }

    public void setLayerPathSelected(String layerPathSelected) {
        this.layerPathSelected = layerPathSelected;
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

    public List<GeoLocationConfig> getFieldsGeolocationSelectedConfigs() {
        return fieldsGeolocationSelectedConfigs;
    }

    public void setFieldsGeolocationSelectedConfigs(List<GeoLocationConfig> fieldsGeolocationSelectedConfigs) {
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
}

package br.imd.aqueducte.models.mongodocuments;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public abstract class ImportationSetup {

    public static final String FILE = "FILE";
    public static final String WEB_SERVICE = "WEB_SERVICE";

    @Id
    private String id;

    @NotBlank(message = "User ID is required")
    private String idUser;

    @Indexed(unique = true)
    @NotBlank(message = "Label required")
    private String label;

    /**
     * importType: FILE or WEB_SERVICE
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

    /**
     * This field is used for check if already some Entity at SGEOL DB
     */
    private String primaryField;

    @JsonIgnore
    private boolean isSelectedGeolocationData;

    private List<String> fieldsGeolocationSelected;

    private List<GeoLocationConfig> fieldsGeolocationSelectedConfigs;

    @CreatedDate
    private Date dateCreated;

    @LastModifiedDate
    private Date dateModified;

}

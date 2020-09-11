package br.imd.aqueducte.models.mongodocuments.external_app_config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document
@Getter
@Setter
@NoArgsConstructor
public class ExternalAppConfig {

    @Id
    private String id;

    @NotBlank
    private String applicationName;

    @NotBlank
    private String hashConfig;

    @NotBlank
    private ServiceConfig authServiceConfig;

    @NotBlank
    private PersistenceServiceConfig persistenceServiceConfig;

    @NotBlank
    private Date createdDate;

    @NotBlank
    private Date updatedDate;
}

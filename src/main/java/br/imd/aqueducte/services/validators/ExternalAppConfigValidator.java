package br.imd.aqueducte.services.validators;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Log4j2
public class ExternalAppConfigValidator {

    private List<String> httpVerbs = Arrays.asList("GET", "POST", "PUT", "PATCH");

    public void validExternalAppConfig(ExternalAppConfig config) throws Exception {
        String errorMessage;

        if (config.getApplicationName() == null || config.getApplicationName() == "") {
            errorMessage = "ExternalAppConfig - applicationName is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (config.getPersistenceServiceConfig() == null) {
            errorMessage = "ExternalAppConfig - persistenceServiceConfig is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (config.getPersistenceServiceConfig().getUrl() == null || config.getPersistenceServiceConfig().getUrl() == "") {
            errorMessage = "ExternalAppConfig - persistenceServiceConfig - url is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (config.getPersistenceServiceConfig().getHttpVerb() == null || config.getPersistenceServiceConfig().getHttpVerb() == "") {
            errorMessage = "ExternalAppConfig - persistenceServiceConfig - httpVerb is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (!httpVerbs.contains(config.getPersistenceServiceConfig().getHttpVerb().toUpperCase())) {
            errorMessage = "ExternalAppConfig - authServiceConfig - httpVerb must be valid";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (config.getPersistenceServiceConfig().getContentType() == null || config.getPersistenceServiceConfig().getContentType() == "") {
            errorMessage = "ExternalAppConfig - persistenceServiceConfig - contentType is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        } else if (config.getPersistenceServiceConfig().getReturnStatusCode() == null) {
            errorMessage = "ExternalAppConfig - persistenceServiceConfig - returnStatus is required";
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

        if (config.getAuthServiceConfig() != null) {
            if (config.getAuthServiceConfig().getUrl() == null || config.getAuthServiceConfig().getUrl() == "") {
                errorMessage = "ExternalAppConfig - authServiceConfig - url is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (config.getAuthServiceConfig().getHttpVerb() == null || config.getAuthServiceConfig().getHttpVerb() == "") {
                errorMessage = "ExternalAppConfig - authServiceConfig - httpVerb is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (!httpVerbs.contains(config.getAuthServiceConfig().getHttpVerb().toUpperCase())) {
                errorMessage = "ExternalAppConfig - authServiceConfig - httpVerb must be valid";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (config.getAuthServiceConfig().getContentType() == null || config.getAuthServiceConfig().getContentType() == "") {
                errorMessage = "ExternalAppConfig - persistenceServiceConfig - contentType is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            } else if (config.getAuthServiceConfig().getReturnStatusCode() == null) {
                errorMessage = "ExternalAppConfig - authServiceConfig - returnStatus is required";
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }
        }
    }
}




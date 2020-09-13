package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import br.imd.aqueducte.models.mongodocuments.external_app_config.ServiceConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface ExternalAppConfigService extends GenericService<ExternalAppConfig> {
    ExternalAppConfig getConfigByHash(String hash) throws Exception;

    void deleteByHash(String hash) throws Exception;

    HttpRequestBase mountExternalAppConfigService(ServiceConfig serviceConfig, Map<String, Object> headers);
}

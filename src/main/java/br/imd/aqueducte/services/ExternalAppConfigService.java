package br.imd.aqueducte.services;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import org.springframework.stereotype.Component;

@Component
public interface ExternalAppConfigService extends GenericService<ExternalAppConfig> {
    ExternalAppConfig getConfigByHash(String hash) throws Exception;

    void deleteByHash(String hash) throws Exception;
}

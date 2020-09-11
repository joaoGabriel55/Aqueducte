package br.imd.aqueducte.repositories;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExternalAppConfigRepository extends MongoRepository<ExternalAppConfig, String> {
    ExternalAppConfig findByHashConfig(String hash);
}

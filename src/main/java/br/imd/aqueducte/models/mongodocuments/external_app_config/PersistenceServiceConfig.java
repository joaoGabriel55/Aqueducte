package br.imd.aqueducte.models.mongodocuments.external_app_config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersistenceServiceConfig extends ServiceConfig {

    private boolean isBatchPersistence;
}

package br.imd.aqueducte.services;

import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import br.imd.aqueducte.models.mongodocuments.external_app_config.PersistenceServiceConfig;
import br.imd.aqueducte.models.mongodocuments.external_app_config.ServiceConfig;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Log4j2
public class ExternalAppConfigServiceTest extends AqueducteApplicationTests {

    @Autowired
    private ExternalAppConfigService service;

    private ExternalAppConfig mockConfig() {
        ExternalAppConfig config = new ExternalAppConfig();
        config.setApplicationName("TestApp");

        ServiceConfig authServiceConfig = new ServiceConfig();
        authServiceConfig.setUrl("http://test.com");
        authServiceConfig.setHttpVerb("GET");
        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put("token", "123456");
        headers.put("token2", "a1b2");
        authServiceConfig.setHeaders(headers);
        authServiceConfig.setReturnStatusCode(200);
        config.setAuthServiceConfig(authServiceConfig);

        PersistenceServiceConfig persistenceServiceConfig = new PersistenceServiceConfig();
        persistenceServiceConfig.setUrl("http://test.com");
        persistenceServiceConfig.setHttpVerb("POST");
        persistenceServiceConfig.setReturnStatusCode(201);
        config.setPersistenceServiceConfig(persistenceServiceConfig);
        return config;
    }

    @Test
    public void createTest() throws Exception {
        ExternalAppConfig config = mockConfig();

        ExternalAppConfig result = service.createOrUpdate(config);
        assertEquals(config.getApplicationName(), result.getApplicationName());
    }

    @Test
    public void getExternalAppConfigByHashTest() throws Exception {
        ExternalAppConfig config = mockConfig();

        ExternalAppConfig result = service.createOrUpdate(config);
        assertEquals(config.getApplicationName(), result.getApplicationName());

        result = service.getConfigByHash(result.getHashConfig());
        assertEquals(config.getApplicationName(), result.getApplicationName());
    }

    @Test
    public void deleteByHashTest() throws Exception {
        ExternalAppConfig config = mockConfig();

        ExternalAppConfig result = service.createOrUpdate(config);
        assertEquals(config.getApplicationName(), result.getApplicationName());

        service.deleteByHash(result.getHashConfig());

        result = service.getConfigByHash(result.getHashConfig());
        assertEquals(null, result);
    }

    @After
    public void tearDown() {
        final String collection = "externalAppConfig";
        log.info("Dropping collection: {} ", collection);
        mongoTemplate.dropCollection(collection);
    }
}

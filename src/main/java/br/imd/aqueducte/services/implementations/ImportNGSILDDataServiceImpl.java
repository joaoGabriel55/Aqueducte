package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import br.imd.aqueducte.models.mongodocuments.external_app_config.PersistenceServiceConfig;
import br.imd.aqueducte.services.ExternalAppConfigService;
import br.imd.aqueducte.services.ImportNGSILDDataService;
import br.imd.aqueducte.utils.RequestsUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static br.imd.aqueducte.utils.RequestsUtils.HASH_CONFIG;
import static br.imd.aqueducte.utils.RequestsUtils.getHttpClientInstance;

@SuppressWarnings("ALL")
@Service
@Log4j2
public class ImportNGSILDDataServiceImpl implements ImportNGSILDDataService {

    @Autowired
    private ExternalAppConfigService externalAppConfigService;

    private boolean isPostHttpVerb(String httpVerb) {
        if (httpVerb.toUpperCase().equals("POST"))
            return true;
        return false;
    }

    @Override
    public void importData(
            String layer, Map<String, String> headers, Map<String, String> allParams, JSONArray jsonArray
    ) throws Exception {
        RequestsUtils requestsUtils = new RequestsUtils();
        ExternalAppConfig config = externalAppConfigService.getConfigByHash(headers.get(HASH_CONFIG));

        PersistenceServiceConfig persistenceConfig = config.getPersistenceServiceConfig();
        if (!isPostHttpVerb(persistenceConfig.getHttpVerb())) {
            String msg = "HTTP Verb must be 'POST'";
            log.error(msg);
            throw new Exception(msg);
        }

        HttpRequestBase persistenceRequest = externalAppConfigService.mountExternalAppConfigService(
                persistenceConfig, allParams, headers
        );

        if (config.getPersistenceServiceConfig().isBatchPersistence()) {
            HttpPost httpPost = (HttpPost) persistenceRequest;
            httpPost.setEntity(requestsUtils.buildEntity(jsonArray));
            HttpResponse response = getHttpClientInstance().execute(httpPost);
            if (response.getStatusLine().getStatusCode() != persistenceConfig.getReturnStatusCode()) {
                String msg = "Error at batch data import";
                log.error(msg);
                throw new Exception(msg);
            }
        } else {
            HttpPost httpPost = (HttpPost) persistenceRequest;
            int index = 0;
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                httpPost.setEntity(new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON));
                HttpResponse response = getHttpClientInstance().execute(httpPost);
                if (response.getStatusLine().getStatusCode() != persistenceConfig.getReturnStatusCode()) {
                    String msg = "Error at data import. Index[" + index + "]";
                    log.error(msg);
                    throw new Exception(msg);
                }
                index++;
            }
        }

    }

}

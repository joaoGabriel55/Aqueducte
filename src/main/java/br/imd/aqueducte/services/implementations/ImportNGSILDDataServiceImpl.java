package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.services.ImportNGSILDDataService;
import br.imd.aqueducte.services.sgeolqueriesservices.EntityOperationsService;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.config.PropertiesParams.*;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.utils.RequestsUtils.getHttpClientInstance;
import static br.imd.aqueducte.utils.RequestsUtils.readBodyReq;

@SuppressWarnings("ALL")
@Service
public class ImportNGSILDDataServiceImpl implements ImportNGSILDDataService {

    private EntityOperationsService entityOperationsService = EntityOperationsService.getInstance();

    public HttpRequestBase requestConfigParams(String url, String appToken, String userToken, JSONArray jsonArray) {
        RequestsUtils requestsUtils = new RequestsUtils();
        HttpPost request = new HttpPost(url);
        // create headers
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        requestsUtils.setHeadersParams(headers, request);
        request.setEntity(requestsUtils.buildEntity(jsonArray));
        return request;
    }

    @Override
    public int updateDataAlreadyImported(String layer, String appToken, String userToken, List<LinkedHashMap<String, Object>> ngsildData, String primaryField) {
        ngsildData.removeIf(entity -> {
            if (!entity.containsKey(primaryField))
                return false;
            Map<String, Object> value = (Map<String, Object>) entity.get(primaryField);
            if (value == null)
                return false;
            List<String> entityId = entityOperationsService.findByDocument(
                    layer, primaryField, value.get("value"), false, appToken, userToken
            );
            if (entityId != null && entityId.size() != 0) {
                if (entityOperationsService.updateEntity(entityId.get(0), entity, layer)) {
                    return true;
                }
                return false;
            }
            return false;
        });
        return ngsildData.size();
    }

    @Override
    public List<String> importData(String layer, String appToken, String userToken, JSONArray jsonArray) throws IOException {
        String url = URL_SGEOL + layer + "/batch";
        if (layer.contains("preprocessing"))
            url = URL_SGEOL + "preprocessing/" + layer;

        HttpResponse responseSGEOL = getHttpClientInstance().execute(requestConfigParams(url, appToken, userToken, jsonArray));

        if (responseSGEOL.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mapFromJson = mapper.readValue(
                    readBodyReq(responseSGEOL.getEntity().getContent()), Map.class
            );
            List<String> entitiesId = (List<String>) mapFromJson.get("entities");
            logInfo("POST /entity imported {}", entitiesId);
            return entitiesId;
        }
        return null;
    }

}

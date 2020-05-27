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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.utils.RequestsUtils.*;

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
    public int updateDataAlreadyImported(
            String layer,
            String sgeolInstance,
            String appToken,
            String userToken,
            List<LinkedHashMap<String, Object>> ngsildData,
            String primaryField
    ) {
        final int[] index = {0};
        ngsildData.removeIf(entity -> {
            logInfo("[" + index[0] + "] Entity ID: {}", entity.get("id"));
            index[0]++;
            if (!entity.containsKey(primaryField))
                return false;
            Map<String, Object> value = (Map<String, Object>) entity.get(primaryField);
            if (value == null)
                return false;
            List<String> entityId = entityOperationsService.findByDocument(
                    sgeolInstance, layer, primaryField, value.get("value"), false, appToken, userToken
            );
            if (entityId != null && entityId.size() != 0) {
                if (entityOperationsService.updateEntity(sgeolInstance, entityId.get(0), appToken, userToken, entity, layer)) {
                    return true;
                }
                return false;
            }
            return false;
        });
        return ngsildData.size();
    }

    @Override
    public List<String> importData(String layer, String sgeolInstance, String appToken, String userToken, JSONArray jsonArray) throws Exception {
        String url = sgeolInstance + "/v2/" + layer + "/batch";
        if (layer.contains("preprocessing"))
            url = sgeolInstance + "/v2/preprocessing/" + layer;

        HttpResponse responseSGEOL = getHttpClientInstance().execute(requestConfigParams(url, appToken, userToken, jsonArray));

        if (responseSGEOL.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED)
            throw new Exception("Error on middleware request");

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapFromJson = mapper.readValue(
                readBodyReq(responseSGEOL.getEntity().getContent()), Map.class
        );
        List<String> entitiesId = (List<String>) mapFromJson.get("entities");
        logInfo("POST /entity imported {}", entitiesId);
        return entitiesId;
    }

}

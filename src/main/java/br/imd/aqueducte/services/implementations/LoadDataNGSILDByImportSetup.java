package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.models.mongodocuments.ImportationSetup;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupContext;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupStandard;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.*;

import static br.imd.aqueducte.config.PropertiesParams.URL_AQUECONNECT;
import static br.imd.aqueducte.utils.RequestsUtils.*;

@SuppressWarnings("ALL")
@Log4j2
public abstract class LoadDataNGSILDByImportSetup {

    protected Map<String, Object> loadDataWebservice(ImportationSetup importationSetup) {
        RequestsUtils requestsUtils = new RequestsUtils();
        Map<String, Object> responseWSResult = new HashMap<>();
        try {
            responseWSResult = requestsUtils.requestToAPI(mountRequestParams(importationSetup));
            log.info("Load Data from API");
        } catch (IOException e) {
            log.error(e.getMessage(), e.getStackTrace());
            e.printStackTrace();
            return null;
        }
        return responseWSResult;
    }

    protected Map<Object, Object> mountRequestParams(ImportationSetup importationSetup) {
        Map<Object, Object> params = new HashMap<>();
        params.put("method", importationSetup.getHttpVerb());
        params.put("url", importationSetup.getBaseUrl() + importationSetup.getPath());
        params.put("headers", importationSetup.getHeadersParameters());
        params.put("params", importationSetup.getQueryParameters());
        params.put("data", importationSetup.getBodyData());
        return params;
    }

    protected Object findDataRecursive(Map<String, Object> responseWSResult, String keyToFind) {
        for (Map.Entry<String, Object> entry : responseWSResult.entrySet()) {
            if (entry.getKey().equals(keyToFind) && entry.getValue() instanceof List) {
                return entry.getValue();
            } else if (entry.getValue() instanceof Map) {
                findDataRecursive((Map<String, Object>) entry.getValue(), keyToFind);
            }
        }
        return null;
    }

    protected List<Map<String, Object>> filterFieldsSelectedIntoArray(
            List<Object> dataCollection,
            ImportationSetupStandard importationSetup
    ) {
        List<Map<String, Object>> dataCollectionFiltered = new ArrayList<>();
        for (Object data : dataCollection) {
            LinkedHashMap<String, Object> dataFiltered = new LinkedHashMap<>();
            Map<String, Object> dataMap = (Map<String, Object>) data;
            for (String fieldSelected : importationSetup.getFieldsSelected()) {
                if (dataMap.containsKey(fieldSelected)) {
                    dataFiltered.put(fieldSelected, dataMap.get(fieldSelected));
                }
            }
            for (GeoLocationConfig fieldGeoLocation : importationSetup.getFieldsGeolocationSelectedConfigs()) {
                if (dataMap.containsKey(fieldGeoLocation.getKey())) {
                    dataFiltered.put(fieldGeoLocation.getKey(), dataMap.get(fieldGeoLocation.getKey()));
                }
            }
            dataCollectionFiltered.add(dataFiltered);
        }
        return dataCollectionFiltered;
    }

    protected List<Map<String, Object>> filterFieldsSelectedIntoArray(
            List<Object> dataCollection,
            ImportationSetupContext importationSetup
    ) {
        List<Map<String, Object>> dataCollectionFiltered = new ArrayList<>();
        for (Object data : dataCollection) {
            LinkedHashMap<String, Object> dataFiltered = new LinkedHashMap<>();
            Map<String, Object> dataMap = (Map<String, Object>) data;
            for (MatchingConfig matchingConfig : importationSetup.getMatchingConfigList()) {
                if (matchingConfig.getForeignProperty() != null &&
                        dataMap.containsKey(matchingConfig.getForeignProperty()) &&
                        !matchingConfig.isLocation()) {
                    dataFiltered.put(
                            matchingConfig.getForeignProperty(),
                            dataMap.get(matchingConfig.getForeignProperty())
                    );
                } else if (matchingConfig.isLocation()) {
                    Map<String, Object> dataMapGeo = filterGeoLocationFieldsSelectedIntoArray(matchingConfig.getGeoLocationConfig(), dataMap);
                    dataFiltered.putAll(dataMapGeo);
                }
            }
            dataCollectionFiltered.add(dataFiltered);
        }
        return dataCollectionFiltered;
    }

    protected Map<String, Object> filterGeoLocationFieldsSelectedIntoArray(
            List<GeoLocationConfig> geoLocationConfig, Map<String, Object> dataMap) {
        Map<String, Object> dataFiltered = new HashMap<>();
        for (GeoLocationConfig config : geoLocationConfig) {
            if (dataMap.containsKey(config.getKey())) {
                dataFiltered.put(config.getKey(), dataMap.get(config.getKey()));
            }
        }
        return dataFiltered;
    }

    protected Map<String, Integer> getFileFields(String sgeolInstance, String userToken, ImportationSetup importationSetup) {
        StringBuilder url = new StringBuilder();
        url.append(URL_AQUECONNECT);
        url.append("file-import-setup-resource/file-fields/");
        url.append(importationSetup.getIdUser());
        url.append("?path=" + importationSetup.getFilePath());
        url.append("&delimiter=" + importationSetup.getDelimiterFileContent());

        HttpGet request = new HttpGet(url.toString());
        request.setHeader(SGEOL_INSTANCE, sgeolInstance);
        request.setHeader(USER_TOKEN, userToken);
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
            if (response.getStatusLine().getStatusCode() == STATUS_OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseMounted = mapper.readValue(
                        readBodyReq(response.getEntity().getContent()), Map.class
                );
                return (Map<String, Integer>) responseMounted.get("fieldsMap");
            } else {
                log.error("Error to get file fields");
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e.getStackTrace());
            e.printStackTrace();
        }
        return null;
    }

    protected List<Map<String, Object>> convertToJSON(
            String sgeolInstance,
            String userToken,
            ImportationSetup importationSetup,
            Map<String, Integer> fieldsSelected
    ) {
        StringBuilder url = new StringBuilder();
        url.append(URL_AQUECONNECT);
        url.append("file-import-setup-resource/convert-to-json/");
        url.append(importationSetup.getIdUser());
        url.append("?path=" + importationSetup.getFilePath());
        url.append("&delimiter=" + importationSetup.getDelimiterFileContent());

        HttpPost request = new HttpPost(url.toString());
        request.setHeader(USER_TOKEN, userToken);
        request.setHeader(SGEOL_INSTANCE, sgeolInstance);
        request.setEntity(objectToJson(fieldsSelected));
        try {
            HttpResponse response = getHttpClientInstance().execute(request);
            if (response.getStatusLine().getStatusCode() == STATUS_OK) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> responseMounted = mapper.readValue(
                        readBodyReq(response.getEntity().getContent()), Map.class
                );
                return (List<Map<String, Object>>) responseMounted.get("data");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e.getStackTrace());
            e.printStackTrace();
            return null;
        }
        return null;
    }


    protected StringEntity objectToJson(Object o) {
        String json = new Gson().toJson(o);
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }
}

package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.pojos.DataSetRelationship;
import br.imd.aqueducte.models.pojos.GeoLocationConfig;
import br.imd.aqueducte.models.pojos.MatchingConfig;
import br.imd.aqueducte.utils.RequestsUtils;
import com.google.gson.Gson;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.*;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@SuppressWarnings("ALL")
public abstract class LoadDataNGSILDByImportationSetup {
    protected Map<String, Object> loadDataWebservice(ImportationSetupWithContext importationSetupWithContext) {
        RequestsUtils requestsUtils = new RequestsUtils();
        Map<String, Object> responseWSResult = new HashMap<>();
        try {
            responseWSResult = requestsUtils.requestToAPI(mountRequestParams(importationSetupWithContext));
            logInfo("Load Data from API", null);
        } catch (IOException e) {
            logError(e.getMessage(), e.getStackTrace());
            e.printStackTrace();
            return null;
        }
        return responseWSResult;
    }

    protected Map<Object, Object> mountRequestParams(ImportationSetupWithContext importationSetupWithContext) {
        Map<Object, Object> params = new HashMap<>();
        params.put("method", importationSetupWithContext.getHttpVerb());
        params.put("url", importationSetupWithContext.getBaseUrl() + importationSetupWithContext.getPath());
        params.put("headers", importationSetupWithContext.getHeadersParameters());
        params.put("params", importationSetupWithContext.getQueryParameters());
        params.put("data", importationSetupWithContext.getBodyData());
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

    protected List<LinkedHashMap<String, Object>> filterFieldsSelectedIntoArray(List<Object> dataCollection, ImportationSetupWithContext importationSetup) {
        List<LinkedHashMap<String, Object>> dataCollectionFiltered = new ArrayList<>();
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

    protected StringEntity getDataSetRelationshipJson(DataSetRelationship dataSetRelationship) {
        String json = new Gson().toJson(dataSetRelationship);
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }
}

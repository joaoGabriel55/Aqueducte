package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.mongodocuments.ImportationSetup;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.dtos.DataSetRelationship;
import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.utils.RequestsUtils;
import com.google.gson.Gson;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.*;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@SuppressWarnings("ALL")
public abstract class LoadDataNGSILDByImportSetup {
    protected Map<String, Object> loadDataWebservice(ImportationSetup importationSetup) {
        RequestsUtils requestsUtils = new RequestsUtils();
        Map<String, Object> responseWSResult = new HashMap<>();
        try {
            responseWSResult = requestsUtils.requestToAPI(mountRequestParams(importationSetup));
            logInfo("Load Data from API", null);
        } catch (IOException e) {
            logError(e.getMessage(), e.getStackTrace());
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

    protected List<LinkedHashMap<String, Object>> filterFieldsSelectedIntoArray(
            List<Object> dataCollection,
            ImportationSetupWithoutContext importationSetup
    ) {
        List<LinkedHashMap<String, Object>> dataCollectionFiltered = new ArrayList<>();
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

    protected List<LinkedHashMap<String, Object>> filterFieldsSelectedIntoArray(
            List<Object> dataCollection,
            ImportationSetupWithContext importationSetup
    ) {
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

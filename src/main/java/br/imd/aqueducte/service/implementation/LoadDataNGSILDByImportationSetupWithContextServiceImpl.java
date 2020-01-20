package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.pojos.DataSetRelationship;
import br.imd.aqueducte.models.pojos.GeoLocationConfig;
import br.imd.aqueducte.models.pojos.MatchingConfig;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.treats.JsonFlatConvertTreat;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import br.imd.aqueducte.utils.RequestsUtils;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.*;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.utils.PropertiesParams.STATUS_OK;
import static br.imd.aqueducte.utils.PropertiesParams.URL_AQUECONNECT;

@SuppressWarnings("ALL")
public class LoadDataNGSILDByImportationSetupWithContextServiceImpl implements LoadDataNGSILDByImportationSetupService<ImportationSetupWithContext> {
    @Override
    public List<LinkedHashMap<String, Object>> loadData(ImportationSetupWithContext importationSetupWithContext) {
        JsonFlatConvertTreat jsonFlatConvertTreat = new JsonFlatConvertTreat();

        // Load data from Webservice
        Map<String, Object> responseWSResult = loadDataWebservice(importationSetupWithContext);
        Map<String, Object> responseWSResultFlat = (Map<String, Object>) jsonFlatConvertTreat.getJsonFlat(responseWSResult);
        // Get data chosen
        Object dataFound = findDataRecursive(responseWSResultFlat, importationSetupWithContext.getDataSelected());
        if (dataFound instanceof List) {
            // Flat Json collection
            List<Object> dataCollectionFlat = (List<Object>) jsonFlatConvertTreat.getJsonFlat(dataFound);
            List<LinkedHashMap<String, Object>> dataForConvert = filterFieldsSelectedIntoArray(dataCollectionFlat, importationSetupWithContext);

            // Convert o NGSI-LD
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            try {
                List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                        importationSetupWithContext.getContextFileLink(),
                        importationSetupWithContext.getMatchingConfigList(),
                        dataForConvert,
                        importationSetupWithContext.getLayerSelected()
                );
                return listConvertedIntoNGSILD;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public int makeDataRelationshipAqueconnect(DataSetRelationship dataSetRelationship) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(URL_AQUECONNECT + "relationships");

        // TODO: Auth - Parsing the "user-token" for Aqueconnect microservice

        request.setEntity(getDataSetRelationshipJson(dataSetRelationship));
        int statusCode = 0;
        try {
            HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            if (response.getStatusLine().getStatusCode() != STATUS_OK) {
                return statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 400;
        }
        return statusCode;
    }

    private Map<String, Object> loadDataWebservice(ImportationSetupWithContext importationSetupWithContext) {
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

    private Map<Object, Object> mountRequestParams(ImportationSetupWithContext importationSetupWithContext) {
        Map<Object, Object> params = new HashMap<>();
        params.put("method", importationSetupWithContext.getHttpVerb());
        params.put("url", importationSetupWithContext.getBaseUrl() + importationSetupWithContext.getPath());
        params.put("headers", importationSetupWithContext.getHeadersParameters());
        params.put("params", importationSetupWithContext.getQueryParameters());
        params.put("data", importationSetupWithContext.getBodyData());
        return params;
    }

    private Object findDataRecursive(Map<String, Object> responseWSResult, String keyToFind) {
        for (Map.Entry<String, Object> entry : responseWSResult.entrySet()) {
            if (entry.getKey().equals(keyToFind) && entry.getValue() instanceof List) {
                return entry.getValue();
            } else if (entry.getValue() instanceof Map) {
                findDataRecursive((Map<String, Object>) entry.getValue(), keyToFind);
            }
        }
        return null;
    }

    private List<LinkedHashMap<String, Object>> filterFieldsSelectedIntoArray(List<Object> dataCollection, ImportationSetupWithContext importationSetup) {
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

    private Map<String, Object> filterGeoLocationFieldsSelectedIntoArray(
            List<GeoLocationConfig> geoLocationConfig, Map<String, Object> dataMap) {
        Map<String, Object> dataFiltered = new HashMap<>();
        for (GeoLocationConfig config : geoLocationConfig) {
            if (dataMap.containsKey(config.getKey())) {
                dataFiltered.put(config.getKey(), dataMap.get(config.getKey()));
            }
        }
        return dataFiltered;
    }

    private StringEntity getDataSetRelationshipJson(DataSetRelationship dataSetRelationship) {
        String json = new Gson().toJson(dataSetRelationship);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        return entity;
    }
}

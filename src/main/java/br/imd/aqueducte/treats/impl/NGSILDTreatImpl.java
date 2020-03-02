package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.treats.impl.NGSILDUtils.removeSpacesForeignProperty;
import static br.imd.aqueducte.utils.FormatterUtils.checkIsGeoJson;
import static br.imd.aqueducte.utils.PropertiesParams.*;
import static br.imd.aqueducte.utils.RequestsUtils.readBodyReq;

// TODO: Create a method for treat geolocation on both conversions cases
@SuppressWarnings("ALL")
public class NGSILDTreatImpl implements NGSILDTreat {

    private final String locationField = "location";

    private NGSILDUtils ngsildUtils;

    public NGSILDTreatImpl() {
        this.ngsildUtils = new NGSILDUtils();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LinkedHashMap<String, Object>> convertToEntityNGSILD(ImportNSILDDataWithoutContextConfig importConfig,
                                                                     String layerPath,
                                                                     Map<Object, Object> contextLink) {

        // Property data provided from external API
        List<Map<String, Object>> dataListFromExternalAPI = importConfig.getDataContentForNGSILDConversion();
        List<LinkedHashMap<String, Object>> listContentConverted = new ArrayList<>();
        // Geolocation config
        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        // Attribute of entity obtained.
        Map.Entry<String, Object> propertiesContent = null;

        for (int i = 0; i < dataListFromExternalAPI.size(); i++) {
            LinkedHashMap<String, Object> linkedHashMapNGSILD = new LinkedHashMap<>();
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            LinkedHashMap<String, Object> ldObj;
            ldObj = (LinkedHashMap<String, Object>) dataListFromExternalAPI.get(i);
            UUID uuid = UUID.randomUUID();
            this.ngsildUtils.initDefaultProperties(linkedHashMapNGSILD, null, layerPath, uuid.toString());
            HashMap<String, Object> typeAndValueMap = new HashMap<>();
            if (ldObj != null) {
                List<Object> listTwoFields = new ArrayList<>();
                for (Iterator<Map.Entry<String, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext(); ) {
                    HashMap<String, Object> objectValue = new HashMap<>();
                    propertiesContent = iterator.next();
                    for (GeoLocationConfig configElem : geoLocationConfig) {
                        if (configElem.getKey().equalsIgnoreCase(propertiesContent.getKey()) && propertiesContent.getValue() != null) {
                            if ((propertiesContent.getValue() instanceof Map) &&
                                    checkIsGeoJson((Map<String, Object>) propertiesContent.getValue())) {
                                Map<String, Object> geoJsonMap = new HashMap<>();
                                geoJsonMap.put("type", "GeoProperty");
                                geoJsonMap.put("value", propertiesContent.getValue());
                                typeAndValueMap.put(locationField, geoJsonMap);
                                propertiesContent.setValue(null);
                            } else {
                                if (!configElem.getTypeOfSelection().equals("twofields")) {
                                    this.ngsildUtils.convertToGeoJson(
                                            objectValue,
                                            propertiesContent.getValue(),
                                            configElem
                                    );
                                    typeAndValueMap.put(locationField, objectValue);
                                    propertiesContent.setValue(null);
                                } else if (configElem.getTypeOfSelection().equals("twofields")) {
                                    listTwoFields.add(propertiesContent.getValue());
                                    if (listTwoFields.size() == 2) {
                                        this.ngsildUtils.convertToGeoJson(
                                                objectValue,
                                                listTwoFields,
                                                configElem
                                        );
                                        typeAndValueMap.put(locationField, objectValue);
                                        listTwoFields = new ArrayList<>();
                                    }
                                    propertiesContent.setValue(null);
                                }
                            }
                            break;
                        }
                    }
                    if (propertiesContent.getValue() != null) {
                        objectValue.put("type", "Property");
                        objectValue.put("value", propertiesContent.getValue());
                        if (propertiesContent.getKey().equals("id") || propertiesContent.getKey().equals("type")) {
                            typeAndValueMap.put(this.ngsildUtils.treatIdOrType(propertiesContent.getKey()), objectValue);
                        } else if (propertiesContent.getKey().contains(".")) {
                            typeAndValueMap.put(propertiesContent.getKey().replace(".", "_"), objectValue);
                        } else {
                            typeAndValueMap.put(propertiesContent.getKey(), objectValue);
                        }
                        objectValue = new HashMap<>();
                    }
                }
            }
            linkedHashMapNGSILD.putAll(typeAndValueMap);
            listContentConverted.add(linkedHashMapNGSILD);
            linkedHashMapNGSILD = new LinkedHashMap<>();
        }
        return listContentConverted;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LinkedHashMap<String, Object>> matchingWithContextAndConvertToEntityNGSILD(
            String contextLink,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath) {
        List<LinkedHashMap<String, Object>> listNGSILD = new ArrayList<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        List<String> contextList = Arrays.asList(contextLink);
        Map<Object, Integer> indexes = new HashMap<>();
        for (Map<String, Object> element : contentForConvert) {
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            UUID uuid = UUID.randomUUID();
            ngsildUtils.initDefaultProperties(properties, contextList, layerPath, uuid.toString());
            List<Object> listTwoFields = new ArrayList<>();
            for (Entry<String, Object> property : element.entrySet()) {
                String key = property.getKey();
                for (MatchingConfig matches : matchingConfig) {

                    String foreignProperty = removeSpacesForeignProperty(matches.getForeignProperty());
                    String contextName = matches.getContextName();
                    Boolean isLocation = matches.isLocation();
                    Boolean hasRelationship = matches.isHasRelationship();

                    if (!this.ngsildUtils.propertyIsLocation(property, isLocation)) {
                        if (key.equalsIgnoreCase(foreignProperty) && matches.isTransientField()) {
                            HashMap<String, Object> typeValue = new HashMap<>();
                            typeValue.put("type", "Transient");
                            typeValue.put("value", property.getValue());
                            properties.put(key, typeValue);
                        } else if (key.equalsIgnoreCase(foreignProperty) && matches.isPrimaryField()) {
                            HashMap<String, Object> typeValue = new HashMap<>();
                            typeValue.put("type", "Primary");
                            typeValue.put("value", property.getValue());
                            properties.put(key, typeValue);
                        } else if (key.equalsIgnoreCase(foreignProperty) && (!isLocation && isLocation != null)) {
                            if (hasRelationship != null && hasRelationship) {
                                HashMap<String, Object> typeValue = new HashMap<>();
                                typeValue.put("type", "Relationship");
                                typeValue.put("object", property.getValue());
                                typeValue.put("relationshipConfig", matches.getRelationshipConfig());
                                properties.put(contextName, typeValue);
                            } else {
                                HashMap<String, Object> typeValue = new HashMap<>();
                                typeValue.put("type", "Property");
                                typeValue.put("value", property.getValue());
                                properties.put(contextName, typeValue);
                            }
                        }
                    } else if (this.ngsildUtils.propertyIsLocation(property, isLocation)) {
                        HashMap<String, Object> valueGeoLocation = new HashMap<>();
                        for (GeoLocationConfig configElem : matches.getGeoLocationConfig()) {
                            if (configElem.getKey().equalsIgnoreCase(property.getKey()) && property.getValue() != null) {
                                if ((property.getValue() instanceof Map) &&
                                        checkIsGeoJson((Map<String, Object>) property.getValue())) {
                                    Map<String, Object> geoJsonMap = new HashMap<>();
                                    geoJsonMap.put("type", "GeoProperty");
                                    geoJsonMap.put("value", property.getValue());
                                    properties.put(contextName, geoJsonMap);
                                    property.setValue(null);
                                } else {
                                    if (!configElem.getTypeOfSelection().equals("twofields")) {
                                        this.ngsildUtils.convertToGeoJson(
                                                valueGeoLocation,
                                                property.getValue(),
                                                configElem
                                        );
                                        properties.put(contextName, valueGeoLocation);
                                        property.setValue(null);
                                    } else if (configElem.getTypeOfSelection().equals("twofields")) {
                                        listTwoFields.add(property.getValue());
                                        if (listTwoFields.size() == 2) {
                                            this.ngsildUtils.convertToGeoJson(
                                                    valueGeoLocation,
                                                    listTwoFields,
                                                    configElem
                                            );
                                            properties.put(contextName, valueGeoLocation);
                                            listTwoFields = new ArrayList<>();
                                        }
                                        property.setValue(null);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!this.ngsildUtils.checkIfEntityAlreadyExistsByPrimaryField(properties, listNGSILD, indexes))
                listNGSILD.add(properties);
            properties = new LinkedHashMap<>();
        }
        return listNGSILD;
    }

    @Override
    public List<String> importToSGEOL(String layer, String appToken, String userToken, JSONArray jsonArray) throws IOException {
        String url = URL_SGEOL + "v2/" + layer + "/batch";
        RequestsUtils requestsUtils = new RequestsUtils();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        // create headers
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put(APP_TOKEN, appToken);
        headers.put(USER_TOKEN, userToken);
        requestsUtils.setHeadersParams(headers, request);
        request.setEntity(requestsUtils.buildEntity(jsonArray));
        HttpResponse responseSGEOL = httpClient.execute(request);

        if (responseSGEOL.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> mapFromJson = mapper.readValue(readBodyReq(responseSGEOL.getEntity().getContent()), Map.class);
            List<String> entitiesId = (List<String>) mapFromJson.get("entities");
            logInfo("POST /entity imported {}", entitiesId);
            return entitiesId;
        }
        return null;
    }

    /*private void dataForGeoJsonFormatProcessor(List<Map<String, String>> geoLocationConfig,
                                                Entry<String, Object> propertiesContent,
                                                HashMap<String, Object> typeAndValueMap,
                                                List<Object> listTwoFields,
                                                HashMap<String, Object> objectValue,
                                                boolean refresh) {
         for (Map<String, String> configElem : geoLocationConfig) {
             if (configElem.get("key").equals(propertiesContent.getKey()) && propertiesContent.getValue() != null) {
                 if ((propertiesContent.getValue() instanceof Map) &&
                         this.ngsildUtils.checkIsGeoJson((Map<String, Object>) propertiesContent.getValue())) {
                     Map<String, Object> geoJsonMap = new HashMap<>();
                     geoJsonMap.put("type", "GeoProperty");
                     geoJsonMap.put("value", propertiesContent.getValue());
                     typeAndValueMap.put(propertiesContent.getKey(), geoJsonMap);
                     propertiesContent.setValue(null);
                 } else {
                     if (!configElem.get("typeOfSelection").equals("twofields")) {
                         convertToGeoJson(
                                 objectValue,
                                 propertiesContent.getValue(),
                                 configElem
                         );
                         typeAndValueMap.put(propertiesContent.getKey(), objectValue);
                         propertiesContent.setValue(null);
                     } else if (configElem.get("typeOfSelection").equals("twofields")) {
                         listTwoFields.add(propertiesContent.getValue());
                         if (listTwoFields.size() == 2) {
                             convertToGeoJson(
                                     objectValue,
                                     listTwoFields,
                                     configElem
                             );
                             typeAndValueMap.put(configElem.get("singleFieldLocation"), objectValue);
                             listTwoFields = new ArrayList<>();
                         }
                         propertiesContent.setValue(null);
                     }
                 }
                 break;
             }

             if (refresh) // Renew the Map for avoid wrong data
                 objectValue = new HashMap<>();
         }
     }*/
}

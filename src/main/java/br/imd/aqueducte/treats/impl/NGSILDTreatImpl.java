package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.models.pojos.GeoLocationConfig;
import br.imd.aqueducte.models.pojos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.pojos.MatchingConfig;
import br.imd.aqueducte.treats.NGSILDTreat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.utils.FormatterUtils.checkIsGeoJson;
import static br.imd.aqueducte.utils.PropertiesParams.APP_TOKEN;
import static br.imd.aqueducte.utils.PropertiesParams.USER_TOKEN;

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
                        if (configElem.getKey().equals(propertiesContent.getKey()) && propertiesContent.getValue() != null) {
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
                        if (propertiesContent.getKey().contains(".")) {
                            typeAndValueMap.put(propertiesContent.getKey().replace(".", "_"), objectValue);
                        } else {
                            typeAndValueMap.put(propertiesContent.getKey(), objectValue);
                        }
                        typeAndValueMap.put(this.ngsildUtils.treatIdOrType(propertiesContent.getKey()), objectValue);
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
            List<LinkedHashMap<String, Object>> contentForConvert,
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

                    String foreignProperty = matches.getForeignProperty();
                    String contextName = matches.getContextName();
                    Boolean isLocation = matches.isLocation();
                    Boolean hasRelationship = matches.isHasRelationship();

                    if (this.ngsildUtils.checkValuesFromKeysAreNotNull(property)) {
                        if (key.equals(foreignProperty) && matches.isTransientField()) {
                            HashMap<String, Object> typeValue = new HashMap<>();
                            typeValue.put("type", "Transient");
                            typeValue.put("value", property.getValue());
                            properties.put(key, typeValue);
                        } else if (key.equals(foreignProperty) && matches.isPrimaryField()) {
                            HashMap<String, Object> typeValue = new HashMap<>();
                            typeValue.put("type", "Primary");
                            typeValue.put("value", property.getValue());
                            properties.put(key, typeValue);
                        } else if (key.equals(foreignProperty) && (!isLocation && isLocation != null)) {
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
                    } else if (isLocation != null && isLocation) {
                        HashMap<String, Object> valueGeoLocation = new HashMap<>();
                        for (GeoLocationConfig configElem : matches.getGeoLocationConfig()) {
                            if (configElem.getKey().equals(property.getKey()) && property.getValue() != null) {
                                if ((property.getValue() instanceof Map) &&
                                        checkIsGeoJson((Map<String, Object>) property.getValue())) {
                                    Map<String, Object> geoJsonMap = new HashMap<>();
                                    geoJsonMap.put("type", "GeoProperty");
                                    geoJsonMap.put("value", property.getValue());
                                    properties.put(contextName, geoJsonMap);
                                    property.setValue(null);
                                } else {
                                    if (!configElem.getTypeGeolocation().equals("twofields")) {
                                        this.ngsildUtils.convertToGeoJson(
                                                valueGeoLocation,
                                                property.getValue(),
                                                configElem
                                        );
                                        properties.put(contextName, valueGeoLocation);
                                        property.setValue(null);
                                    } else if (configElem.getTypeGeolocation().equals("twofields")) {
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
    public List<String> importToSGEOL(String url, String appToken, String userToken, JSONArray jsonArray) {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set custom header
        headers.set(APP_TOKEN, appToken);
        headers.set(USER_TOKEN, userToken);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        // use `exchange` method for HTTP call
        List<String> jsonArrayResponse = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            HttpEntity<String> entity = new HttpEntity<>(jsonArray.get(i).toString(), headers);
            ResponseEntity<String> responseSGEOL = restTemplate.exchange(url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<String>() {
                    });
            if (responseSGEOL.getStatusCode() == HttpStatus.CREATED) {
                jsonArrayResponse.add(jsonObject.get("id").toString());
            }
        }
        return jsonArrayResponse;
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

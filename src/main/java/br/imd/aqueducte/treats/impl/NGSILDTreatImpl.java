package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.utils.NGSILDUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.utils.PropertiesParams.APP_TOKEN;
import static br.imd.aqueducte.utils.PropertiesParams.USER_TOKEN;

// TODO: Create a method for treat geo location on both conversions cases
public class NGSILDTreatImpl implements NGSILDTreat {

    private final String locationField = "location";

    private NGSILDUtils ngsildUtils;

    public NGSILDTreatImpl() {
        this.ngsildUtils = new NGSILDUtils();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LinkedHashMap<String, Object>> convertToEntityNGSILD(Map<String, Object> data,
                                                                     String layerPath,
                                                                     Map<Object, Object> contextLink) {

        LinkedHashMap<String, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

        HashMap<String, Object> objectValue = new HashMap<>();

        // Property data provided from external API
        List<Object> dataListFromExternalAPI = (List<Object>) data.get("dataContentForNGSILDConversion");
        List<LinkedHashMap<String, Object>> listContentConverted = new ArrayList<>();

        // Geolocation config
        List<Map<String, Object>> geoLocationConfig = (List<Map<String, Object>>) data.get("geoLocationConfig");

        // Attribute of entity obtained.
        Map.Entry<String, Object> propertiesContent = null;

        for (int i = 0; i < dataListFromExternalAPI.size(); i++) {
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            LinkedHashMap<String, Object> ldObj;
            ldObj = (LinkedHashMap<String, Object>) dataListFromExternalAPI.get(i);
            UUID uuid = UUID.randomUUID();
            ngsildUtils.initDefaultProperties(linkedHashMapNGSILD, null, layerPath, uuid.toString());

            HashMap<String, Object> typeAndValueMap = new HashMap<>();

            if (ldObj != null) {
                List<Object> listTwoFields = new ArrayList<>();
                for (Iterator<Map.Entry<String, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext(); ) {
                    propertiesContent = iterator.next();
                    for (Map<String, Object> configElem : geoLocationConfig) {
                        if (configElem.get("key").equals(propertiesContent.getKey()) && propertiesContent.getValue() != null) {
                            if ((propertiesContent.getValue() instanceof Map) &&
                                    this.ngsildUtils.checkIsGeoJson((Map<String, Object>) propertiesContent.getValue())) {
                                Map<String, Object> geoJsonMap = new HashMap<>();
                                geoJsonMap.put("type", "GeoProperty");
                                geoJsonMap.put("value", propertiesContent.getValue());
                                typeAndValueMap.put(locationField, geoJsonMap);
                                propertiesContent.setValue(null);
                            } else {
                                if (!configElem.get("typeOfSelection").equals("twofields")) {
                                    convertToGeoJson(
                                            objectValue,
                                            propertiesContent.getValue(),
                                            configElem
                                    );
                                    typeAndValueMap.put(locationField, objectValue);
                                    propertiesContent.setValue(null);
                                } else if (configElem.get("typeOfSelection").equals("twofields")) {
                                    listTwoFields.add(propertiesContent.getValue());
                                    if (listTwoFields.size() == 2) {
                                        convertToGeoJson(
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
                        objectValue = new HashMap<>();
                    }
                    if (propertiesContent.getValue() != null) {
                        objectValue.put("type", "Property");
                        objectValue.put("value", propertiesContent.getValue());
                        if (propertiesContent.getKey() == "type" || propertiesContent.getKey() == "id") {
                            typeAndValueMap.put(propertiesContent.getKey() + "_", objectValue);
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
            List<LinkedHashMap<String, Object>> matchingConfig,
            List<LinkedHashMap<String, Object>> contentForConvert,
            String layerPath) {
        List<LinkedHashMap<String, Object>> listNGSILD = new ArrayList<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        List<String> contextList = Arrays.asList(contextLink);
        for (Map<String, Object> element : contentForConvert) {
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            UUID uuid = UUID.randomUUID();
            ngsildUtils.initDefaultProperties(properties, contextList, layerPath, uuid.toString());
            List<Object> listTwoFields = new ArrayList<>();
            for (Entry<String, Object> property : element.entrySet()) {
                String key = property.getKey();
                for (Object matches : matchingConfig) {
                    LinkedHashMap<Object, Object> keyMatch = (LinkedHashMap<Object, Object>) matches;

                    String foreignProperty = getValueMatching(keyMatch.get("foreignProperty"));
                    String contextName = getValueMatching(keyMatch.get("contextName"));
                    Boolean isLocation = (Boolean) keyMatch.get("isLocation");
                    Boolean hasRelationship = (Boolean) keyMatch.get("hasRelationship");

                    if (key.equals(foreignProperty) && (!isLocation && isLocation != null)) {
                        if (checkValuesFromKeysAreNull(property)) {
                            if (hasRelationship != null && hasRelationship) {
                                HashMap<String, Object> typeValue = new HashMap<>();
                                typeValue.put("type", "Relationship");
                                typeValue.put("object", property.getValue());
                                typeValue.put("relationshipConfig", keyMatch.get("relationshipConfig"));

                                properties.put(contextName, typeValue);
                                break;
                            } else {
                                HashMap<String, Object> typeValue = new HashMap<>();
                                typeValue.put("type", "Property");
                                typeValue.put("value", property.getValue());
                                properties.put(contextName, typeValue);
                                break;
                            }
                        }
                    } else if (isLocation != null && isLocation) {
                        HashMap<String, Object> valueGeoLocation = new HashMap<>();
                        List<Map<String, Object>> geoLocationConfig = (List<Map<String, Object>>) keyMatch.get("geoLocationConfig");
                        for (Map<String, Object> configElem : geoLocationConfig) {
                            if (configElem.get("key").equals(property.getKey()) && property.getValue() != null) {
                                if ((property.getValue() instanceof Map) &&
                                        this.ngsildUtils.checkIsGeoJson((Map<String, Object>) property.getValue())) {
                                    Map<String, Object> geoJsonMap = new HashMap<>();
                                    geoJsonMap.put("type", "GeoProperty");
                                    geoJsonMap.put("value", property.getValue());
                                    properties.put(contextName, geoJsonMap);
                                    property.setValue(null);
                                } else {
                                    if (!configElem.get("typeOfSelection").equals("twofields")) {
                                        convertToGeoJson(
                                                valueGeoLocation,
                                                property.getValue(),
                                                configElem
                                        );
                                        properties.put(contextName, valueGeoLocation);
                                        property.setValue(null);
                                    } else if (configElem.get("typeOfSelection").equals("twofields")) {
                                        listTwoFields.add(property.getValue());
                                        if (listTwoFields.size() == 2) {
                                            convertToGeoJson(
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
                                break;
                            }
                        }
                    }
                }
            }
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

    // private void dataForGeoJsonFormatProcessor(List<Map<String, String>> geoLocationConfig,
    //                                            Entry<String, Object> propertiesContent,
    //                                            HashMap<String, Object> typeAndValueMap,
    //                                            List<Object> listTwoFields,
    //                                            HashMap<String, Object> objectValue,
    //                                            boolean refresh) {
    //     for (Map<String, String> configElem : geoLocationConfig) {
    //         if (configElem.get("key").equals(propertiesContent.getKey()) && propertiesContent.getValue() != null) {
    //             if ((propertiesContent.getValue() instanceof Map) &&
    //                     this.ngsildUtils.checkIsGeoJson((Map<String, Object>) propertiesContent.getValue())) {
    //                 Map<String, Object> geoJsonMap = new HashMap<>();
    //                 geoJsonMap.put("type", "GeoProperty");
    //                 geoJsonMap.put("value", propertiesContent.getValue());
    //                 typeAndValueMap.put(propertiesContent.getKey(), geoJsonMap);
    //                 propertiesContent.setValue(null);
    //             } else {
    //                 if (!configElem.get("typeOfSelection").equals("twofields")) {
    //                     convertToGeoJson(
    //                             objectValue,
    //                             propertiesContent.getValue(),
    //                             configElem
    //                     );
    //                     typeAndValueMap.put(propertiesContent.getKey(), objectValue);
    //                     propertiesContent.setValue(null);
    //                 } else if (configElem.get("typeOfSelection").equals("twofields")) {
    //                     listTwoFields.add(propertiesContent.getValue());
    //                     if (listTwoFields.size() == 2) {
    //                         convertToGeoJson(
    //                                 objectValue,
    //                                 listTwoFields,
    //                                 configElem
    //                         );
    //                         typeAndValueMap.put(configElem.get("singleFieldLocation"), objectValue);
    //                         listTwoFields = new ArrayList<>();
    //                     }
    //                     propertiesContent.setValue(null);
    //                 }
    //             }
    //             break;
    //         }

    //         if (refresh) // Renew the Map for avoid wrong data
    //             objectValue = new HashMap<>();
    //     }
    // }

    private void convertToGeoJson(HashMap<String, Object> value, Object coordinates, Map<String, Object> configGeoLocation) {
        value.put("type", "GeoProperty");
        HashMap<Object, Object> valueGeometry = new HashMap<>();

        if (!configGeoLocation.get("typeGeolocation").equals("Point")) {
            if (coordinates instanceof ArrayList) {
                valueGeometry.put("coordinates", parseDoubleCoordinates((List<Object>) coordinates, configGeoLocation));
            } else if (coordinates instanceof String) {
                String[] coordinatesString = ((String) coordinates).split((String) configGeoLocation.get("delimiter"));
                List<List<Object>> coordinatesTreated = parseDoubleCoordinates(Arrays.asList(coordinatesString), configGeoLocation);
                valueGeometry.put("coordinates", coordinatesTreated);
            }
        } else {
            if (coordinates instanceof ArrayList) {
                valueGeometry.put("coordinates", parseDoubleCoordinates((List<Object>) coordinates, configGeoLocation).get(0));
            } else if (coordinates instanceof String) {
                String[] coordinatesString = ((String) coordinates).split((String) configGeoLocation.get("delimiter"));
                List<List<Object>> coordinatesTreated = parseDoubleCoordinates(Arrays.asList(coordinatesString), configGeoLocation);
                valueGeometry.put("coordinates", coordinatesTreated.get(0));
            }
        }
        valueGeometry.put("type", configGeoLocation.get("typeGeolocation"));
        value.put("value", valueGeometry);
    }

    private List<List<Object>> parseDoubleCoordinates(List<Object> coordinates, Map<String, Object> configGeoLocation) {
        List<Object> coordinatesDoubleType = new ArrayList<>();
        List<List<Object>> coordinatesFinal = new ArrayList<>();
        for (Object coordinate : coordinates) {
            if (coordinate instanceof String)
                coordinatesDoubleType.add(Double.parseDouble(coordinate.toString().replace(",", ".")));
            else
                coordinatesDoubleType.add(coordinate);
        }

        // invertCoords
        String typeOfSelection = (String) configGeoLocation.get("typeGeolocation");
        Boolean invertCoords = (Boolean) configGeoLocation.get("invertCoords");

        if (typeOfSelection.equals("Point") && (invertCoords != null && invertCoords))
            Collections.reverse(coordinatesDoubleType);

        coordinatesFinal.add(coordinatesDoubleType);
        return coordinatesFinal;
    }

    private String getValueMatching(Object value) {
        if (value != null)
            return value.toString();
        return null;
    }

    private boolean checkValuesFromKeysAreNull(Entry<String, Object> property) {
        if (property.getValue() != null && property.getValue() != "") {
            return true;
        }
        return false;
    }


    @Override
    public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entityToImport) {
        return null;
    }
}

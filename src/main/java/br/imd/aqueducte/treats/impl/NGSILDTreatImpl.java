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

import static br.imd.aqueducte.utils.RequestsUtils.APP_TOKEN;
import static br.imd.aqueducte.utils.RequestsUtils.USER_TOKEN;

public class NGSILDTreatImpl implements NGSILDTreat {
    @SuppressWarnings("unchecked")
    @Override
    public List<LinkedHashMap<String, Object>> convertToEntityNGSILD(List<Object> data, String layerPath,
                                                                     Map<Object, Object> contextLink) {

        LinkedHashMap<String, Object> linkedHashMapNGSILD = new LinkedHashMap<>();

        HashMap<String, Object> objectValue = new HashMap<>();

        // Property data provided from external API
        ArrayList<Object> dataListFromExternalAPI = (ArrayList<Object>) data;

        List<LinkedHashMap<String, Object>> listObjForSgeol = new ArrayList<>();

        // Attribute of entity obtained.
        Map.Entry<String, Object> propertiesContent = null;

        for (int i = 0; i < dataListFromExternalAPI.size(); i++) {
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            LinkedHashMap<String, Object> ldObj;
            ldObj = (LinkedHashMap<String, Object>) dataListFromExternalAPI.get(i);
            UUID uuid = UUID.randomUUID();
            ngsildUtils.initDefaultProperties(linkedHashMapNGSILD, null, layerPath, uuid.toString());

            HashMap<String, HashMap<String, Object>> typeAndValueMap = new HashMap<>();

            if (ldObj != null) {
                for (Iterator<Map.Entry<String, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext(); ) {
                    propertiesContent = iterator.next();
                    if (propertiesContent.getValue() != null && propertiesContent.getValue() instanceof LinkedHashMap) {
                        LinkedHashMap<Object, Object> linkedHashMap = (LinkedHashMap<Object, Object>) propertiesContent
                                .getValue();
                        if (linkedHashMap.containsKey("isLocation") && linkedHashMap.containsKey("coordinates")) {
                            convertToGeoJson(
                                    objectValue,
                                    (List<Object>) linkedHashMap.get("coordinates"),
                                    (String) linkedHashMap.get("typeGeolocation")
                            );
                            typeAndValueMap.put(propertiesContent.getKey(), objectValue);
                        }
                    } else {
                        objectValue.put("type", "Property");
                        objectValue.put("value", propertiesContent.getValue());
                        if (propertiesContent.getKey() == "type" || propertiesContent.getKey() == "id") {
                            typeAndValueMap.put(propertiesContent.getKey() + "_", objectValue);
                        } else if (propertiesContent.getKey().contains(".")) {
                            typeAndValueMap.put(propertiesContent.getKey().replace(".", "_"), objectValue);
                        } else {
                            typeAndValueMap.put(propertiesContent.getKey(), objectValue);
                        }
                    }
                    objectValue = new HashMap<>();
                }
            }
            linkedHashMapNGSILD.putAll(typeAndValueMap);
            listObjForSgeol.add(linkedHashMapNGSILD);
            linkedHashMapNGSILD = new LinkedHashMap<>();
        }

        return listObjForSgeol;
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
            for (Entry<String, Object> property : element.entrySet()) {
                String key = property.getKey();
                for (Object matches : matchingConfig) {
                    LinkedHashMap<Object, Object> keyMatch = (LinkedHashMap<Object, Object>) matches;
                    String foreignProperty = getValueMatching(keyMatch.get("foreignProperty"));
                    String contextName = getValueMatching(keyMatch.get("contextName"));
                    Boolean isLocation = (Boolean) keyMatch.get("isLocation");
                    if (key.equals(foreignProperty) && (!isLocation && isLocation != null)) {
                        if (checkValuesFromKeysAreNull(property)) {
                            HashMap<String, Object> typeValue = new HashMap<String, Object>();
                            typeValue.put("type", "Property");
                            typeValue.put("value", property.getValue());
                            properties.put(contextName, typeValue);
                            break;
                        }
                    } else if (isLocation && isLocation != null) {
                        HashMap<String, Object> valueGeoLocation = new HashMap<>();
                        convertToGeoJson(
                                valueGeoLocation,
                                (List<Object>) ((HashMap<String, Object>) property.getValue()).get("coordinates"),
                                (String) ((HashMap<String, Object>) property.getValue()).get("typeGeolocation")
                        );
                        properties.put(contextName, valueGeoLocation);
                        break;
                    }
                }
            }
            listNGSILD.add(properties);
            properties = new LinkedHashMap<>();
        }
        return listNGSILD;
    }

//    private HashMap<String, HashMap<String, Object>> convertIntoTypeValueObject(
//            HashMap<String, HashMap<String, Object>> typeAndValueMap,
//            Map.Entry<String, Object> propertiesContent,
//            HashMap<String, Object> content) {
//        content.put("type", "Property");
//        content.put("value", propertiesContent.getValue());
//        if (propertiesContent.getKey() == "type" || propertiesContent.getKey() == "id") {
//            typeAndValueMap.put(propertiesContent.getKey() + "_", content);
//        } else if (propertiesContent.getKey().contains(".")) {
//            typeAndValueMap.put(propertiesContent.getKey().replace(".", "_"), content);
//        } else {
//            typeAndValueMap.put(propertiesContent.getKey(), content);
//        }
//        return typeAndValueMap;
//    }


    private List<List<Object>> parseDoubleCoordinates(List<Object> coordinates) {
        List<Object> coordinatesDoubleType = new ArrayList<>();
        List<List<Object>> coordinatesFinal = new ArrayList<>();
        for (Object coordinate : coordinates) {
            coordinatesDoubleType.add(Double.parseDouble(coordinate.toString().replace(",", ".")));
        }
        coordinatesFinal.add(coordinatesDoubleType);
        return coordinatesFinal;
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

    private void convertToGeoJson(HashMap<String, Object> value, List<Object> coordinates, String type) {
        value.put("type", "GeoProperty");
        HashMap<Object, Object> valueGeometry = new HashMap<>();

        if (!type.equals("Point")) {
            if (!(coordinates instanceof ArrayList))
                valueGeometry.put("coordinates", parseDoubleCoordinates(coordinates));
            else
                valueGeometry.put("coordinates", coordinates);
        } else {
            valueGeometry.put("coordinates", parseDoubleCoordinates(coordinates).get(0));
        }
        valueGeometry.put("type", type);
        value.put("value", valueGeometry);
    }


    private String getValueMatching(Object value) {
        if (value != null)
            return value.toString();
        return null;
    }

    private boolean checkValuesFromKeysAreNull(Entry<String, Object> property) {
        if (property.getValue() != null &&
                property.getValue() != "") {
            return true;
        }
        return false;
    }


    @Override
    public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entityToImport) {
        return null;
    }
}

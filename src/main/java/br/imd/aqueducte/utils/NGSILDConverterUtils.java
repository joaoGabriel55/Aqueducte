package br.imd.aqueducte.utils;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.imd.aqueducte.utils.GeoJsonValidator.getGeoJson;

@SuppressWarnings("ALL")
@Log4j2
public class NGSILDConverterUtils {

    public static final String LOCATION_FIELD = "location";

    public static final List<String> GEO_PROPERTY_TYPE = Arrays.asList("location", "observationSpace", "operationSpace");

    private static NGSILDConverterUtils instance;

    public static NGSILDConverterUtils getInstance() {
        if (instance == null) {
            return instance = new NGSILDConverterUtils();
        }
        return instance;
    }

    public String treatIdOrType(String key) {
        if (key.equals("type") || key.equals("id")) {
            return key + "_";
        }
        return key;
    }

    public static String removeSpacesForeignProperty(String value) {
        if (value != null)
            return value.toLowerCase().replaceAll(" ", "_").trim();
        return null;
    }

    public void initDefaultProperties(
            String sgeolInstance,
            Map<String, Object> linkedHashMapNGSILD,
            List<String> contextList,
            String layerType,
            String uuid
    ) {
        List<String> contextListDefault = new ArrayList<>();
        contextListDefault
                .add("https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");

        if (contextList != null) {
            String contextSource = sgeolInstance + "/v2/context-sources/";
            List<String> contextLinks = contextList.stream().map((link) -> contextSource + link).collect(Collectors.toList());
            contextListDefault.addAll(contextLinks);
        }

        linkedHashMapNGSILD.put("@context", contextListDefault);
        linkedHashMapNGSILD.put("id", "urn:ngsi-ld:" + layerType + ":" + uuid);
        linkedHashMapNGSILD.put("type", layerType);
    }

    public boolean checkIfEntityAlreadyExistsByPrimaryField(
            Map<String, Object> object,
            List<LinkedHashMap<String, Object>> listNGSILD,
            Map<Object, Integer> indexes
    ) {
        Object primaryId = getValuePrimary(object);
        boolean alreadyExistByPrimaryField = false;
        List<Object> listId = new ArrayList<>();
        if (primaryId != null) {
            for (Map<String, Object> obj : listNGSILD) {
                Object primaryIdLocal = getValuePrimary(obj);
                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Map) {
                        Map<String, Object> objectMap = (Map<String, Object>) value;
                        if (Objects.equals(primaryIdLocal, primaryId) && objectMap.get("type").equals("Relationship")) {
                            Object objValue = ((Map<String, Object>) obj.get(key)).get("object");
                            Object valueToAdd = ((Map<String, Object>) object.get(key)).get("object");
                            if (!(objValue instanceof List) && !Objects.equals(valueToAdd, objValue)) {
                                listId.add(objValue);
                                listId.add(valueToAdd);
                                ((Map<String, Object>) obj.get(key)).put("object", listId);
                            } else if (objValue instanceof List &&
                                    !((List<Object>) ((Map<String, Object>) obj.get(key)).get("object")).contains(valueToAdd)) {
                                ((List<Object>) ((Map<String, Object>) obj.get(key)).get("object"))
                                        .add(((Map<String, Object>) object.get(key)).get("object"));
                            }
                            alreadyExistByPrimaryField = true;
                            return alreadyExistByPrimaryField;
                        }
                    }
                }
            }
        }
        return alreadyExistByPrimaryField;
    }

    private Object getValuePrimary(Map<String, Object> objectMap) {
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) entry.getValue();
                if (map.containsKey("type") && map.get("type").equals("Primary"))
                    return map.get("value");
            }
        }
        return null;
    }

    public void convertToGeoJson(HashMap<String, Object> value, Object coordinates, GeoLocationConfig configGeoLocation) {
        value.put("type", "GeoProperty");
        HashMap<Object, Object> valueGeometry = new HashMap<>();

        if (!configGeoLocation.getTypeGeolocation().equals("Point")) {
            if (coordinates instanceof ArrayList) {
                valueGeometry.put("coordinates", parseDoubleCoordinates((List<Object>) coordinates, configGeoLocation));
            } else if (coordinates instanceof String) {
                String[] coordinatesString = ((String) coordinates).split((String) configGeoLocation.getDelimiter());
                List<List<Object>> coordinatesTreated = parseDoubleCoordinates(Arrays.asList(coordinatesString), configGeoLocation);
                valueGeometry.put("coordinates", coordinatesTreated);
            }
        } else {
            if (coordinates instanceof ArrayList) {
                valueGeometry.put("coordinates", parseDoubleCoordinates((List<Object>) coordinates, configGeoLocation).get(0));
            } else if (coordinates instanceof String) {
                String[] coordinatesString = ((String) coordinates).split((String) configGeoLocation.getDelimiter());
                List<List<Object>> coordinatesTreated = parseDoubleCoordinates(Arrays.asList(coordinatesString), configGeoLocation);
                valueGeometry.put("coordinates", coordinatesTreated.get(0));
            }
        }
        valueGeometry.put("type", configGeoLocation.getTypeGeolocation());
        value.put("value", valueGeometry);
    }

    private List<List<Object>> parseDoubleCoordinates(List<Object> coordinates, GeoLocationConfig configGeoLocation) {
        List<Object> coordinatesDoubleType = new ArrayList<>();
        List<List<Object>> coordinatesFinal = new ArrayList<>();
        for (Object coordinate : coordinates) {
            if (coordinate instanceof String)
                coordinatesDoubleType.add(Double.parseDouble(coordinate.toString().replace(",", ".")));
            else
                coordinatesDoubleType.add(coordinate);
        }

        String typeGeolocation = (String) configGeoLocation.getTypeGeolocation();
        Boolean invertCoords = (Boolean) configGeoLocation.isInvertCoords();

        if (typeGeolocation.equals("Point") && (invertCoords != null && invertCoords))
            Collections.reverse(coordinatesDoubleType);

        coordinatesFinal.add(coordinatesDoubleType);
        return coordinatesFinal;
    }

    private String getValueMatching(Object value) {
        if (value != null)
            return value.toString();
        return null;
    }

    public boolean propertyIsLocation(Object value, Boolean isLocation) {
        if (value != null)
            return value != "" && (isLocation != null && isLocation);
        return false;
    }

    public LinkedHashMap<String, Object> propertyGeoJsonFormat(List<GeoLocationConfig> geoLocationConfig,
                                                               Map.Entry<String, Object> property,
                                                               List<Object> listTwoFields,
                                                               LinkedHashMap<String, Object> properties,
                                                               String contextName) {
        HashMap<String, Object> valueGeoLocation = new HashMap<>();
        for (GeoLocationConfig configElem : geoLocationConfig) {
            if (configElem.getKey().equalsIgnoreCase(property.getKey()) && property.getValue() != null) {
                Map<String, Object> geojson = null;
                try {
                    geojson = getGeoJson(property.getValue());
                } catch (JSONException e) {
                    continue;
                } catch (IOException e) {
                    continue;
                }
                if (geojson != null) {
                    Map<String, Object> geoJsonMap = new HashMap<>();
                    geoJsonMap.put("type", "GeoProperty");
                    geoJsonMap.put("value", geojson);
                    properties.put(contextName, geoJsonMap);
                    property.setValue(null);
                } else {
                    if (!configElem.getTypeOfSelection().equals("twofields")) {
                        convertToGeoJson(
                                valueGeoLocation,
                                property.getValue(),
                                configElem
                        );
                        properties.put(contextName, valueGeoLocation);
                        property.setValue(null);
                    } else if (configElem.getTypeOfSelection().equals("twofields")) {
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
            }
        }
        return properties;
    }

    public void validNGSILDGeoPropertyType(String finalProperty, String key) throws Exception {
        if (!GEO_PROPERTY_TYPE.contains(finalProperty) || !GEO_PROPERTY_TYPE.contains(key)) {
            String msg = "GeoProperty type must be: [\"location\", \"observationSpace\", \"operationSpace\"].";
            log.error(msg);
            throw new Exception(msg);
        }
    }

    private void validTwoFieldsSelection(LinkedHashMap<String, GeoLocationConfig> configElem) throws Exception {
        boolean isTwoFields = configElem.values().stream()
                .filter(elem -> elem.getTypeOfSelection().equals("twofields"))
                .collect(Collectors.toList())
                .size() == 2;
        if (!isTwoFields) {
            String msg = "typeOfSelection must contain 2 setups for type twofields";
            log.error(msg);
            throw new Exception(msg);
        }
    }

    public LinkedHashMap<String, Object> geoJsonConverterFormat(
            LinkedHashMap<String, GeoLocationConfig> configElem,
            Map<String, Object> property,
            String propertyName,
            LinkedHashMap<String, Object> properties,
            String finalProperty
    ) throws Exception {
        HashMap<String, Object> valueGeoLocation = new HashMap<>();
        GeoLocationConfig configGeoElem = configElem.get(propertyName);
        Map<String, Object> geojson = null;
        if (configGeoElem != null && !configGeoElem.getTypeOfSelection().equals("twofields") && configElem.size() != 2) {
            try {
                geojson = getGeoJson(property.get(propertyName));
            } catch (JSONException e) {
                log.warn(e.getMessage());
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
            if (geojson != null) {
                Map<String, Object> geoJsonMap = new HashMap<>();
                geoJsonMap.put("type", "GeoProperty");
                geoJsonMap.put("value", geojson);
                properties.put(finalProperty, geoJsonMap);
            } else {
                if (property.get(propertyName) != null) {
                    convertToGeoJson(valueGeoLocation, property.get(propertyName), configGeoElem);
                    properties.put(finalProperty, valueGeoLocation);
                }
            }
        } else if (configElem != null && configElem.size() == 2) {
            validTwoFieldsSelection(configElem);
            List<Object> coordsList = configElem.keySet().stream()
                    .filter(key -> property.get(key) != null)
                    .map(key -> property.get(key))
                    .collect(Collectors.toList());
            if (coordsList != null && !coordsList.isEmpty()) {
                convertToGeoJson(valueGeoLocation, coordsList, configElem.values().stream().findFirst().get());
                properties.put(finalProperty, valueGeoLocation);
            }
        }
        return properties;
    }
}

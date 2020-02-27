package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;

import java.util.*;

@SuppressWarnings("ALL")
public class NGSILDUtils {
    public String treatIdOrType(String key) {
        if (key.equals("type") || key.equals("id")) {
            return key + "_";
        }
        return key;
    }

    public static String removeSpacesForeignProperty(String value) {
        if (value != null)
            return value.toLowerCase().replaceAll("\\s+", "").trim();
        return null;
    }

    public void initDefaultProperties(
            Map<String, Object> linkedHashMapNGSILD,
            List<String> contextList,
            String layerType,
            String uuid
    ) {

        List<String> contextListDefault = new ArrayList<>();
        contextListDefault
                .add("https://forge.etsi.org/gitlab/NGSI-LD/NGSI-LD/raw/master/coreContext/ngsi-ld-core-context.jsonld");
        if (contextList != null) {
            contextListDefault.addAll(contextList);
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

        // invertCoords
        String typeOfSelection = (String) configGeoLocation.getTypeGeolocation();
        Boolean invertCoords = (Boolean) configGeoLocation.isInvertCoords();

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

    public boolean propertyIsLocation(Map.Entry<String, Object> property, Boolean isLocation) {
        if (property != null) {
            return property.getValue() != null && property.getValue() != "" && (isLocation != null && isLocation);
        }
        return false;
    }
}

package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.treats.NGSILDTreat;

import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.treats.impl.NGSILDUtils.removeSpacesForeignProperty;

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

        for (int i = 0; i < dataListFromExternalAPI.size(); i++) {
            LinkedHashMap<String, Object> linkedHashMapNGSILD = new LinkedHashMap<>();
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            LinkedHashMap<String, Object> ldObj;
            ldObj = (LinkedHashMap<String, Object>) dataListFromExternalAPI.get(i);
            UUID uuid = UUID.randomUUID();
            this.ngsildUtils.initDefaultProperties(linkedHashMapNGSILD, null, layerPath, uuid.toString());
            LinkedHashMap<String, Object> typeAndValueMap = new LinkedHashMap<>();
            if (ldObj != null) {
                List<Object> listTwoFields = new ArrayList<>();
                for (Iterator<Map.Entry<String, Object>> iterator = ldObj.entrySet().iterator(); iterator.hasNext(); ) {
                    HashMap<String, Object> objectValue = new HashMap<>();
                    Map.Entry<String, Object> property = iterator.next();
                    if (geoLocationConfig != null && geoLocationConfig.size() > 0) {
                        typeAndValueMap.putAll(this.ngsildUtils.propertyGeoJsonFormat(
                                geoLocationConfig,
                                property,
                                listTwoFields,
                                typeAndValueMap,
                                locationField
                        ));
                    }
                    if (property.getValue() != null) {
                        objectValue.put("type", "Property");
                        objectValue.put("value", property.getValue());
                        if (property.getKey().equals("id") || property.getKey().equals("type")) {
                            typeAndValueMap.put(this.ngsildUtils.treatIdOrType(property.getKey()), objectValue);
                        } else if (property.getKey().contains(".")) {
                            typeAndValueMap.put(property.getKey().replace(".", "_"), objectValue);
                        } else {
                            typeAndValueMap.put(property.getKey(), objectValue);
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
            List<String> contextLinks,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath) {
        List<LinkedHashMap<String, Object>> listNGSILD = new ArrayList<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        List<String> contextList = contextLinks;
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

                    if (!this.ngsildUtils.propertyIsLocation(property, isLocation)) {
                        if (key.equalsIgnoreCase(foreignProperty) && matches.isTemporaryField()) {
                            properties.put(foreignProperty, getTempProperty(property.getValue()));
                        } else if (key.equalsIgnoreCase(foreignProperty) && (!isLocation && isLocation != null)) {
                            HashMap<String, Object> typeValue = new HashMap<>();
                            typeValue.put("type", "Property");
                            typeValue.put("value", property.getValue());
                            properties.put(contextName, typeValue);
                        }
                    } else if (this.ngsildUtils.propertyIsLocation(property, isLocation)) {
                        properties.putAll(this.ngsildUtils.propertyGeoJsonFormat(
                                matches.getGeoLocationConfig(),
                                property,
                                listTwoFields,
                                properties,
                                contextName
                        ));
                    }
                }
            }
            if (!this.ngsildUtils.checkIfEntityAlreadyExistsByPrimaryField(properties, listNGSILD, indexes))
                listNGSILD.add(properties);
            properties = new LinkedHashMap<>();
        }
        return listNGSILD;
    }

    private Map<String, Object> getTempProperty(Object propertyValue) {
        HashMap<String, Object> tempPropertyValue = new HashMap<>();
        tempPropertyValue.put("type", "TempProperty");
        tempPropertyValue.put("value", propertyValue);
        HashMap<String, Object> value = new HashMap<>();
        value.put("type", "Property");
        value.put("value", tempPropertyValue);
        return value;
    }
}

package br.imd.aqueducte.treats.impl;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.utils.NGSILDUtils;

import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.utils.NGSILDUtils.removeSpacesForeignProperty;

public class NGSILDTreatImpl implements NGSILDTreat {
    private static final String LOCATION_FIELD = "location";

    private final NGSILDUtils ngsildUtils;

    public NGSILDTreatImpl() {
        this.ngsildUtils = new NGSILDUtils();
    }

    @Override
    public List<LinkedHashMap<String, Object>> convertToEntityNGSILD(
            String sgeolInstance,
            ImportNSILDDataWithoutContextConfig importConfig,
            String layerPath,
            Map<Object, Object> contextLink) {

        // Property data provided from external API
        List<Map<String, Object>> dataListFromExternalAPI = importConfig.getDataContentForNGSILDConversion();
        List<LinkedHashMap<String, Object>> listContentConverted = new ArrayList<>();

        // Geolocation config
        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        for (Map<String, Object> objectMap : dataListFromExternalAPI) {
            LinkedHashMap<String, Object> linkedHashMapNGSILD = new LinkedHashMap<>();
            LinkedHashMap<String, Object> ldObj;
            ldObj = (LinkedHashMap<String, Object>) objectMap;
            UUID uuid = UUID.randomUUID();
            this.ngsildUtils.initDefaultProperties(sgeolInstance, linkedHashMapNGSILD, null, layerPath, uuid.toString());
            LinkedHashMap<String, Object> typeAndValueMap = new LinkedHashMap<>();
            if (ldObj != null) {
                List<Object> listTwoFields = new ArrayList<>();
                for (Entry<String, Object> property : ldObj.entrySet()) {
                    HashMap<String, Object> objectValue = new HashMap<>();
                    if (geoLocationConfig != null && geoLocationConfig.size() > 0) {
                        typeAndValueMap.putAll(this.ngsildUtils.propertyGeoJsonFormat(
                                geoLocationConfig,
                                property,
                                listTwoFields,
                                typeAndValueMap,
                                LOCATION_FIELD
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
                    }
                }
            }
            linkedHashMapNGSILD.putAll(typeAndValueMap);
            listContentConverted.add(linkedHashMapNGSILD);
        }
        return listContentConverted;
    }

    @Override
    public List<LinkedHashMap<String, Object>> matchingWithContextAndConvertToEntityNGSILD(
            String sgeolInstance,
            List<String> contextLinks,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    ) {
        List<LinkedHashMap<String, Object>> listNGSILD = new ArrayList<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        for (Map<String, Object> element : contentForConvert) {
            NGSILDUtils ngsildUtils = new NGSILDUtils();
            UUID uuid = UUID.randomUUID();
            ngsildUtils.initDefaultProperties(sgeolInstance, properties, contextLinks, layerPath, uuid.toString());
            List<Object> listTwoFields = new ArrayList<>();
            for (Entry<String, Object> property : element.entrySet()) {
                String key = removeSpacesForeignProperty(property.getKey());
                for (MatchingConfig matches : matchingConfig) {
                    String foreignProperty = removeSpacesForeignProperty(matches.getForeignProperty());
                    String contextName = matches.getContextName();
                    Boolean isLocation = matches.isLocation();
                    // getTempProperty()
                    if (!this.ngsildUtils.propertyIsLocation(property, isLocation)) {
                        if (key.equalsIgnoreCase(foreignProperty) && matches.isTemporaryField()) {
                            String foreignPropertyTreated = this.ngsildUtils.treatIdOrType(foreignProperty);
                            if (!properties.containsKey(foreignPropertyTreated))
                                properties.put(foreignPropertyTreated, typeValue(property.getValue()));
                        } else if (key.equalsIgnoreCase(foreignProperty) && !isLocation) {
                            if (!properties.containsKey(contextName))
                                properties.put(contextName, typeValue(property.getValue()));
                        }
                    } else if (this.ngsildUtils.propertyIsLocation(property, isLocation)) {
                        if (matches.isTemporaryField()) {
                            if (!properties.containsKey(LOCATION_FIELD)) {
                                properties.putAll(this.ngsildUtils.propertyGeoJsonFormat(
                                        matches.getGeoLocationConfig(),
                                        property,
                                        listTwoFields,
                                        properties,
                                        LOCATION_FIELD
                                ));
                            }
                        } else {
                            if (!properties.containsKey(contextName)) {
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
                }
            }
            listNGSILD.add(properties);
            properties = new LinkedHashMap<>();
        }
        return listNGSILD;
    }

    private Map<String, Object> typeValue(Object value) {
        HashMap<String, Object> typeValue = new HashMap<>();
        typeValue.put("type", "Property");
        typeValue.put("value", value);
        return typeValue;
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

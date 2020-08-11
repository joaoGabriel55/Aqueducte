package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDStandardDataConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import br.imd.aqueducte.services.NGSILDConverterService;
import br.imd.aqueducte.utils.NGSILDConverterUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.utils.NGSILDConverterUtils.LOCATION_FIELD;
import static br.imd.aqueducte.utils.NGSILDConverterUtils.removeSpacesForeignProperty;

@Service
@Log4j2
public class NGSILDConverterServiceImpl implements NGSILDConverterService {
    private final NGSILDConverterUtils ngsildConverterUtils = NGSILDConverterUtils.getInstance();

    @Override
    public List<LinkedHashMap<String, Object>> standardConverterNGSILD(
            String sgeolInstance,
            ImportNSILDStandardDataConfig importConfig,
            String layerPath,
            Map<Object, Object> contextLink) throws Exception {

        // Property data provided from external API
        List<Map<String, Object>> dataListFromExternalAPI = importConfig.getDataContentForNGSILDConversion();
        if (dataListFromExternalAPI == null) {
            log.error("dataListFromExternalAPI is null");
            throw new Exception();
        }
        if (dataListFromExternalAPI.size() == 0) {
            log.error("dataListFromExternalAPI is empty");
            throw new Exception();
        }

        List<LinkedHashMap<String, Object>> listContentConverted = new ArrayList<>();

        // Geolocation config
        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        for (Map<String, Object> objectMap : dataListFromExternalAPI) {
            LinkedHashMap<String, Object> linkedHashMapNGSILD = new LinkedHashMap<>();
            LinkedHashMap<String, Object> ldObj;
            ldObj = (LinkedHashMap<String, Object>) objectMap;
            UUID uuid = UUID.randomUUID();
            this.ngsildConverterUtils.initDefaultProperties(sgeolInstance, linkedHashMapNGSILD, null, layerPath, uuid.toString());
            LinkedHashMap<String, Object> typeAndValueMap = new LinkedHashMap<>();
            if (ldObj != null) {
                List<Object> listTwoFields = new ArrayList<>();
                for (Entry<String, Object> property : ldObj.entrySet()) {
                    HashMap<String, Object> objectValue = new HashMap<>();
                    if (geoLocationConfig != null && geoLocationConfig.size() > 0) {
                        typeAndValueMap.putAll(this.ngsildConverterUtils.propertyGeoJsonFormat(
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
                            typeAndValueMap.put(this.ngsildConverterUtils.treatIdOrType(property.getKey()), objectValue);
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
        if (listContentConverted.size() == 0) {
            log.error("listContentConverted is empty");
        }
        log.info("listContentConverted successfully");
        return listContentConverted;
    }

    @Override
    public List<LinkedHashMap<String, Object>> contextConverterNGSILD(
            String sgeolInstance,
            List<String> contextLinks,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    ) throws Exception {
        if (contentForConvert == null) {
            log.error("contentForConvert is null");
            throw new Exception();
        }
        if (contentForConvert.size() == 0) {
            log.error("contentForConvert is empty");
            throw new Exception();
        }
        List<LinkedHashMap<String, Object>> listNGSILD = new ArrayList<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        for (Map<String, Object> element : contentForConvert) {
            UUID uuid = UUID.randomUUID();
            this.ngsildConverterUtils.initDefaultProperties(sgeolInstance, properties, contextLinks, layerPath, uuid.toString());
            List<Object> listTwoFields = new ArrayList<>();
            for (Entry<String, Object> property : element.entrySet()) {
                String key = removeSpacesForeignProperty(property.getKey());
                for (MatchingConfig matches : matchingConfig) {
                    String foreignProperty = removeSpacesForeignProperty(matches.getForeignProperty());
                    String contextName = matches.getContextName();
                    Boolean isLocation = matches.isLocation();
                    // getTempProperty()
                    if (!this.ngsildConverterUtils.propertyIsLocation(property, isLocation)) {
                        if (key.equalsIgnoreCase(foreignProperty) && matches.isTemporaryField()) {
                            String foreignPropertyTreated = this.ngsildConverterUtils.treatIdOrType(foreignProperty);
                            if (!properties.containsKey(foreignPropertyTreated))
                                properties.put(foreignPropertyTreated, typeValue(property.getValue()));
                        } else if (key.equalsIgnoreCase(foreignProperty) && !isLocation) {
                            if (!properties.containsKey(contextName))
                                properties.put(contextName, typeValue(property.getValue()));
                        }
                    } else if (this.ngsildConverterUtils.propertyIsLocation(property, isLocation)) {
                        if (matches.isTemporaryField()) {
                            if (!properties.containsKey(LOCATION_FIELD)) {
                                properties.putAll(this.ngsildConverterUtils.propertyGeoJsonFormat(
                                        matches.getGeoLocationConfig(),
                                        property,
                                        listTwoFields,
                                        properties,
                                        LOCATION_FIELD
                                ));
                            }
                        } else {
                            if (!properties.containsKey(contextName)) {
                                properties.putAll(this.ngsildConverterUtils.propertyGeoJsonFormat(
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
        if (listNGSILD.size() == 0) {
            log.error("listNGSILD is empty");
        }
        log.info("listNGSILD successfully");
        return listNGSILD;
    }

    @Override
    public List<LinkedHashMap<String, Object>> converterNGSILD(
            String instanceUri,
            List<String> contextLinks,
            LinkedHashMap<String, MatchingConverterSetup> matchingConverterSetup,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    ) throws Exception {
        if (contentForConvert == null) {
            log.error("contentForConvert is null");
            throw new Exception();
        }
        if (contentForConvert.size() == 0) {
            log.error("contentForConvert is empty");
            throw new Exception();
        }

        List<LinkedHashMap<String, Object>> listNGSILD = new ArrayList<>();
        LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
        for (Map<String, Object> element : contentForConvert) {
            UUID uuid = UUID.randomUUID();
            this.ngsildConverterUtils.initDefaultProperties(
                    instanceUri, properties, contextLinks, layerPath, uuid.toString()
            );
            for (Entry<String, MatchingConverterSetup> setupEntry : matchingConverterSetup.entrySet()) {
                String key = removeSpacesForeignProperty(setupEntry.getKey());
                MatchingConverterSetup setup = setupEntry.getValue();
                Boolean isLocation = setup.isLocation();
                if (element.containsKey(key) && !isLocation) {
                    if (!properties.containsKey(setup.getFinalProperty())) {
                        properties.put(setup.getFinalProperty(), typeValue(element.get(key)));
                    }
                } else if (setup.getGeoLocationConfig() != null && setup.getGeoLocationConfig().size() != 0 && isLocation) {
                    this.ngsildConverterUtils.validNGSILDGeoPropertyType(setup.getFinalProperty(), setupEntry.getKey());
                    properties.putAll(this.ngsildConverterUtils.geoJsonConverterFormat(
                            setup.getGeoLocationConfig(),
                            element,
                            setup.getForeignProperty(),
                            properties,
                            setup.getFinalProperty()
                    ));
                }
            }
            listNGSILD.add(properties);
            properties = new LinkedHashMap<>();
        }
        if (listNGSILD.size() == 0)
            log.warn("listNGSILD is empty");

        log.info("listNGSILD successfully");
        return listNGSILD;
    }

    private Map<String, Object> typeValue(Object value) {
        HashMap<String, Object> typeValue = new HashMap<>();
        typeValue.put("type", "Property");
        typeValue.put("value", value);
        return typeValue;
    }
}

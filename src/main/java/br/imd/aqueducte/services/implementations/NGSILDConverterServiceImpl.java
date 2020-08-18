package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import br.imd.aqueducte.services.NGSILDConverterService;
import br.imd.aqueducte.utils.NGSILDConverterUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

import static br.imd.aqueducte.utils.NGSILDConverterUtils.removeSpacesForeignProperty;

@Service
@Log4j2
public class NGSILDConverterServiceImpl implements NGSILDConverterService {
    private final NGSILDConverterUtils ngsildConverterUtils = NGSILDConverterUtils.getInstance();

    @Override
    public List<LinkedHashMap<String, Object>> convertIntoNGSILD(
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
                String finalProperty = this.ngsildConverterUtils.treatIdOrType(setup.getFinalProperty());

                if (element.containsKey(key) && !isLocation) {
                    if (!properties.containsKey(finalProperty)) {
                        properties.put(finalProperty, typeValue(element.get(key)));
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

package br.imd.aqueducte.services;

import br.imd.aqueducte.models.dtos.MatchingConverterSetup;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface NGSILDConverterService {

    List<LinkedHashMap<String, Object>> convertIntoNGSILD(
            String instanceUri,
            List<String> contextLinks,
            LinkedHashMap<String, MatchingConverterSetup> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    ) throws Exception;
}

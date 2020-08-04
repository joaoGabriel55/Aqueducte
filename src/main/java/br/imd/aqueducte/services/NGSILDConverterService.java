package br.imd.aqueducte.services;

import br.imd.aqueducte.models.dtos.ImportNSILDStandardDataConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface NGSILDConverterService {
    List<LinkedHashMap<String, Object>> standardConverterNGSILD(
            String sgeolInstance,
            ImportNSILDStandardDataConfig importConfig,
            String layerPath,
            Map<Object, Object> contextLink) throws Exception;

    List<LinkedHashMap<String, Object>> contextConverterNGSILD(
            String sgeolInstance,
            List<String> context,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    ) throws Exception;
}

package br.imd.aqueducte.treats;

import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NGSILDTreat {
    List<LinkedHashMap<String, Object>> convertToEntityNGSILD(
            String sgeolInstance,
            ImportNSILDDataWithoutContextConfig importConfig,
            String layerPath,
            Map<Object, Object> contextLink) throws Exception;

    List<LinkedHashMap<String, Object>> matchingWithContextAndConvertToEntityNGSILD(
            String sgeolInstance,
            List<String> context,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    ) throws Exception;
}

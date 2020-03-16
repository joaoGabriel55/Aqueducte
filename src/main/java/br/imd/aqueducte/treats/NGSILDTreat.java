package br.imd.aqueducte.treats;

import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.dtos.MatchingConfig;
import org.json.JSONArray;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NGSILDTreat {
    List<LinkedHashMap<String, Object>> convertToEntityNGSILD(
            ImportNSILDDataWithoutContextConfig importConfig,
            String layerPath,
            Map<Object, Object> contextLink);

    List<LinkedHashMap<String, Object>> matchingWithContextAndConvertToEntityNGSILD(
            List<String> context,
            List<MatchingConfig> matchingConfig,
            List<Map<String, Object>> contentForConvert,
            String layerPath
    );
}

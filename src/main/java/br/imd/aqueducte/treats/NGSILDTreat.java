package br.imd.aqueducte.treats;

import br.imd.aqueducte.models.pojos.MatchingConfig;
import org.json.JSONArray;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NGSILDTreat {
    List<LinkedHashMap<String, Object>> convertToEntityNGSILD(
            Map<String, Object> data,
            String layerPath,
            Map<Object, Object> contextLink);

    List<LinkedHashMap<String, Object>> matchingWithContextAndConvertToEntityNGSILD(
            String context,
            List<MatchingConfig> matchingConfig,
            List<LinkedHashMap<String, Object>> contentForConvert,
            String layerPath
    );

    List<String> importToSGEOL(String url, String appToken, String userToken, JSONArray jsonArray);

    Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport);
}

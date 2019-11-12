package br.imd.aqueducte.treats;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

public interface NGSILDTreat {
    List<LinkedHashMap<String, Object>> convertToEntityNGSILD(
            List<Object> data,
            String layerPath,
            Map<Object, Object> contextLink);

    List<LinkedHashMap<String, Object>> matchingWithContextAndConvertToEntityNGSILD(
            List<LinkedHashMap<String, Object>> matchingConfig,
            List<LinkedHashMap<String, Object>> contentForConvert,
            String layerPath
    );

    List<String> importToSGEOL(String url, String appToken, String userToken, JSONArray jsonArray);

    Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport);
}

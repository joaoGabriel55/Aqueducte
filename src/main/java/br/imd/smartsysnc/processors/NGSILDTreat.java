package br.imd.smartsysnc.processors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface NGSILDTreat {
    List<LinkedHashMap<Object, Object>> convertToEntityNGSILD(
            List<Object> data,
            String layerPath,
            Map<Object, Object> contextLink);

    void matchingWithContext(
            List<Object> listMatches,
            List<LinkedHashMap<Object, Object>> listNGSILD,
            HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext
    );

    List<String> importToSGEOL(String url, String appToken, String userToken, JSONArray jsonArray);

    Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport);
}

package br.imd.aqueducte.services;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public interface ImportNGSILDDataService {

    int updateDataAlreadyImported(
            String layer,
            String sgeolInstance,
            String appToken,
            String userToken,
            List<LinkedHashMap<String, Object>> ngsildData,
            String primaryField
    );

    List<String> importData(String layer, String sgeolInstance, String appToken, String userToken, JSONArray jsonArray) throws Exception;
}

package br.imd.aqueducte.services;

import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public interface ImportNGSILDDataService {

    void updateDataAlreadyImported(
            String layer,
            String appToken,
            String userToken,
            List<LinkedHashMap<String, Object>> ngsildData,
            String primaryField
    );

    List<String> importData(String layer, String appToken, String userToken, JSONArray jsonArray) throws IOException;
}

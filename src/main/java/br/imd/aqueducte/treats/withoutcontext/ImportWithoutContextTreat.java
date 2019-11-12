package br.imd.aqueducte.treats.withoutcontext;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.aqueducte.models.documents.ImportationSetupWithoutContext;

public class ImportWithoutContextTreat {

    public ImportationSetupWithoutContext convertToImportationSetupModel(Map<String, Object> objectMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) objectMap.get("importationSetupWithoutContext");

        String jsonImportationSetupWithoutContext = new JSONObject(linkedHashMap).toString();

        ImportationSetupWithoutContext impSetupWithoutCxtConverted = mapper.readValue(jsonImportationSetupWithoutContext, ImportationSetupWithoutContext.class);
        return impSetupWithoutCxtConverted;
    }

}

package br.imd.aqueducte.processors.withoutcontext;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.imd.aqueducte.models.ImportationSetupWithoutContext;
import br.imd.aqueducte.processors.impl.NGSILDTreatImpl;

public class ImportWithoutContextTreat extends NGSILDTreatImpl {

    public ImportationSetupWithoutContext convertToImportationSetupModel(Map<String, Object> objectMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        @SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> linkedHashMap = (LinkedHashMap<String, Object>) objectMap.get("importationSetupWithoutContext");

        String jsonImportationSetupWithoutContext = new JSONObject(linkedHashMap).toString();

        ImportationSetupWithoutContext impSetupWithoutCxtConverted = mapper.readValue(jsonImportationSetupWithoutContext, ImportationSetupWithoutContext.class);
        return impSetupWithoutCxtConverted;
    }

    @Override
    public void matchingWithContext(List<Object> listMatches, List<LinkedHashMap<Object, Object>> listNGSILD,
                                    HashMap<Object, HashMap<Object, Object>> propertiesBasedOnContext) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<Object, Object> getLinkedIdListForImportDataToSGEOL(Map<Object, Object> entidadeToImport) {
        // TODO Auto-generated method stub
        return null;
    }

}

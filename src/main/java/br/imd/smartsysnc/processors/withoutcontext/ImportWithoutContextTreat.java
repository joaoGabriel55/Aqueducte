package br.imd.smartsysnc.processors.withoutcontext;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.processors.impl.NGSILDTreatImpl;
import br.imd.smartsysnc.utils.NGSILDUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ImportWithoutContextTreat extends NGSILDTreatImpl {

    public ImportationSetupWithoutContext convertToImportationSetupModel(Map<String, Object> objectMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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

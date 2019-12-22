package br.imd.aqueducte.restservices.convertionNgsild;

import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// type Relationship -> child
//                   -> parent
//                   -> biDirection
public class RelationshipStrategyTest {
    private static String PATH = "/home/quaresma/git/Aqueducte/src/main/resources/files/";
    private static String ALUNOS = PATH + "alunos.json";
    private static String ESCOLAS = PATH + "escolas.json";

    @Test
    public void main() {

        RelationshipStrategyTest relationshipStrategyTest = new RelationshipStrategyTest();

        try {
            List<LinkedHashMap<String, Object>> alunoNGSILD = relationshipStrategyTest.getObjectNGSILD("aluno", ALUNOS);
            // List<LinkedHashMap<String, Object>> alunoNGSILDMatching = relationshipStrategyTest.getContent(ALUNOS, "matchingConfigContent");

            List<LinkedHashMap<String, Object>> escolaNGSILD = relationshipStrategyTest.getObjectNGSILD("escola", ESCOLAS);
            // List<LinkedHashMap<String, Object>> escolaNGSILDMatching = relationshipStrategyTest.getContent(ESCOLAS, "matchingConfigContent");

            for (LinkedHashMap<String, Object> escola : escolaNGSILD) {
                for (LinkedHashMap<String, Object> aluno : alunoNGSILD) {
                    escola.forEach((ki, vi) -> {
                        AtomicInteger count = new AtomicInteger();
                        aluno.forEach((kj, vj) -> {
                            String type = relationshipStrategyTest.getTypeValue(aluno.get(kj));
                            if (type == "Relationship") {
                                Object alunoObj = ((Map<String, Object>) aluno.get(kj)).get("object");
                                Map<String, Object> relationshipConfig = (Map<String, Object>) ((Map<String, Object>) aluno.get(kj)).get("relationshipConfig");
                                if (relationshipConfig != null && ki.equals(relationshipConfig.get("identityField").toString())) {
                                    Object value = ((Map<String, Object>) vi).get("value");
                                    if (alunoObj == value) {
                                        ((Map<String, Object>) aluno.get(kj)).put("object", escola.get("id"));
                                        ((Map<String, Object>) aluno.get(kj)).remove("relationshipConfig");
                                    }
                                }
                            }
                        });
                    });
                }
            }

//            List<LinkedHashMap<String, Object>> alunoNGSILDFiltered =
//                    alunoNGSILD
//                            .stream()
//                            .filter(elem -> !elem.containsKey("relationshipConfig"))
//                            .collect(Collectors.toList());
//
            System.out.println(new JSONArray(escolaNGSILD));
            System.out.println(new JSONArray(alunoNGSILD));

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private String getTypeValue(Object content) {
        if (content instanceof ArrayList) {
            return "";
        } else if (content instanceof Map) {
            if (((Map<String, Object>) content).containsKey("type")) {
                return ((Map<String, Object>) content).get("type").toString();
            } else {
                return "";
            }
        }
        return "";
    }

    private List<LinkedHashMap<String, Object>> getObjectNGSILD(
            String layerPath,
            String jsonFile
    ) throws IOException {
        String contextLink = "https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/city/City_Context.jsonld";
        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();

        List<LinkedHashMap<String, Object>> matchingConfig = getContent(jsonFile, "matchingConfigContent");
        List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion = getContent(jsonFile, "dataContentForNGSILDConversion");
        List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                contextLink,
                matchingConfig,
                dataContentForNGSILDConversion,
                layerPath
        );

        return listConvertedIntoNGSILD;
    }

    private List<LinkedHashMap<String, Object>> getContent(String jsonFile, String key) throws IOException {
        RequestsUtils requestsUtils = new RequestsUtils();
        InputStream inputStream = new FileInputStream(jsonFile);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonObject = mapper.readValue(requestsUtils.readBodyReq(inputStream), Map.class);
        List<LinkedHashMap<String, Object>> matchingConfigContent = (List<LinkedHashMap<String, Object>>) jsonObject.get(key);
        return matchingConfigContent;
    }

}

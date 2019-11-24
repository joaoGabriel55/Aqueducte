package br.imd.aqueducte;

import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class RelationshipStrategy {

    public static void main(String[] args) {

        RelationshipStrategy relationshipStrategy = new RelationshipStrategy();

        try {
            List<LinkedHashMap<String, Object>> matchingConfigContentEscola = new ArrayList<>();
            List<LinkedHashMap<String, Object>> matchingConfigContentAluno = new ArrayList<>();

            List<LinkedHashMap<String, Object>> alunoNGSILD = relationshipStrategy.getObjectNGSILD("aluno", "/home/quaresma/git/Aqueducte/src/main/resources/aluno.json", matchingConfigContentEscola);
            List<LinkedHashMap<String, Object>> escolaNGSILD = relationshipStrategy.getObjectNGSILD("escola", "/home/quaresma/git/Aqueducte/src/main/resources/escola.json", matchingConfigContentAluno);

            System.out.println(alunoNGSILD);
            System.out.println(escolaNGSILD);

//            for (LinkedHashMap<String, Object> escola : escolaNGSILD) {
//                for (LinkedHashMap<String, Object> aluno : alunoNGSILD) {
////                    matchingConfigContentEscola.get("relationshipReferenceConfig");
//                }
//            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    //    "/home/quaresma/git/Aqueducte/src/main/resources/aluno.json"
    private List<LinkedHashMap<String, Object>> getObjectNGSILD(String layerPath, String jsonFile, List<LinkedHashMap<String, Object>> matchingConfigContent) throws IOException {
        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        InputStream inputStream = new FileInputStream(jsonFile);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonObject = mapper.readValue(RequestsUtils.readBodyReq(inputStream), Map.class);

        String contextLink = "https://github.com/JorgePereiraUFRN/SGEOL-LD/blob/master/ngsi-ld/city/City_Context.jsonld";
        matchingConfigContent = (List<LinkedHashMap<String, Object>>) jsonObject.get("matchingConfigContent");
        List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion = (List<LinkedHashMap<String, Object>>) jsonObject.get("dataContentForNGSILDConversion");
        List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                contextLink,
                matchingConfigContent,
                dataContentForNGSILDConversion,
                layerPath
        );

        return listConvertedIntoNGSILD;
    }

}

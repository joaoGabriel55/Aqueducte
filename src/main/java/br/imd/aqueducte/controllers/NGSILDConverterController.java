package br.imd.aqueducte.controllers;

import br.imd.aqueducte.logger.LoggerMessage;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@RestController
@RequestMapping("/sync/ngsildConverter")
@CrossOrigin(origins = "*")
public class NGSILDConverterController {

    @PostMapping(value = "/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertToNGSILDWithoutContext(
            @PathVariable(required = true) String layerPath, @RequestBody List<Object> data) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        try {
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            List<LinkedHashMap<String, Object>> listNGSILD;
            long startTime = System.nanoTime();
            listNGSILD = ngsildTreat.convertToEntityNGSILD(data, layerPath, null);
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            logInfo("Time to conversion NGSI-LD: {}", timeElapsed);
            response.setData(listNGSILD);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * @param dataForConvertIntoNGSILDByContext Contains the lists of matching config and data list for be converted
     *                                          following that matching config.
     */
    @PostMapping(value = "/withMatchingConfig/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertMatchingConfigIntoNGSILD(
            @PathVariable String layerPath,
            @RequestBody Map<String, Object> dataForConvertIntoNGSILDByContext) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        try {
            String contextLink = (String) dataForConvertIntoNGSILDByContext.get("contextLink");
            List<LinkedHashMap<String, Object>> matchingConfigContent = (List<LinkedHashMap<String, Object>>) dataForConvertIntoNGSILDByContext.get("matchingConfigContent");
            List<LinkedHashMap<String, Object>> dataContentForNGSILDConversion = (List<LinkedHashMap<String, Object>>) dataForConvertIntoNGSILDByContext.get("dataContentForNGSILDConversion");
            long startTime = System.nanoTime();
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    contextLink,
                    matchingConfigContent,
                    dataContentForNGSILDConversion,
                    layerPath
            );
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            logInfo("Time to convert MatchingConfig Into NGSI-LD: {}", timeElapsed);
            response.setData(listConvertedIntoNGSILD);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

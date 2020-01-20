package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.pojos.MatchingConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/sync/ngsildConverter")
@CrossOrigin(origins = "*")
public class NGSILDConverterController {

    @PostMapping(value = "/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertToNGSILDWithoutContext(
            @PathVariable String layerPath,
            @RequestBody Map<String, Object> data) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        List<Map<String, Object>> geoLocationConfig = (List<Map<String, Object>>) data.get("geoLocationConfig");
        if (geoLocationConfig.size() > 2) {
            String errGeoLocMessage = "Somente é permitido um campo para geolocalização. Tamanho atual: {}";
            response.getErrors().add(errGeoLocMessage);
            logError(errGeoLocMessage, geoLocationConfig.size());
            return ResponseEntity.badRequest().body(response);
        }

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
            logError(e.getMessage(), e.getStackTrace());
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
            List<MatchingConfig> matchingConfigContent = ((List<LinkedHashMap<String, Object>>) dataForConvertIntoNGSILDByContext.get("matchingConfigContent"))
                    .stream().map(elem -> {
                        MatchingConfig matchingConfig = new MatchingConfig();
                        matchingConfig.fromLinkedHashMap(elem);
                        return matchingConfig;
                    }).collect(Collectors.toList());
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
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

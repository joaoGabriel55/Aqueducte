package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithContextConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

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
            @RequestBody ImportNSILDDataWithoutContextConfig importConfig) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
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
            listNGSILD = ngsildTreat.convertToEntityNGSILD(importConfig, layerPath, null);
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

    @PostMapping(value = "/withMatchingConfig/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertMatchingConfigIntoNGSILD(
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithContextConfig importContextConfig) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        try {
            long startTime = System.nanoTime();
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    importContextConfig.getContextLink(),
                    importContextConfig.getMatchingConfigContent(),
                    importContextConfig.getDataContentForNGSILDConversion(),
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

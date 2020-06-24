package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithContextConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import static br.imd.aqueducte.utils.RequestsUtils.SGEOL_INSTANCE;

@SuppressWarnings("ALL")
@RestController
@Log4j2
@RequestMapping("/sync/ngsildConverter")
@CrossOrigin(origins = "*")
public class NGSILDConverterController {

    @PostMapping(value = "/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertToNGSILDWithoutContext(
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithoutContextConfig importConfig) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        if (geoLocationConfig.size() > 2) {
            String errGeoLocMessage = "Somente é permitido um campo para geolocalização. Tamanho atual: {}";
            response.getErrors().add(errGeoLocMessage);
            log.error(errGeoLocMessage, geoLocationConfig.size());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            List<LinkedHashMap<String, Object>> listNGSILD;
            long startTime = System.currentTimeMillis();
            listNGSILD = ngsildTreat.convertToEntityNGSILD(sgeolInstance, importConfig, layerPath, null);
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            log.info("Time to convert Standard mode Into NGSI-LD: {} ms", timeElapsed);
            response.setData(listNGSILD);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/withMatchingConfig/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertMatchingConfigIntoNGSILD(
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithContextConfig importContextConfig) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        try {
            long startTime = System.currentTimeMillis();
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    sgeolInstance,
                    importContextConfig.getContextLinks(),
                    importContextConfig.getMatchingConfigContent(),
                    importContextConfig.getDataContentForNGSILDConversion(),
                    layerPath
            );
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            log.info("Time to convert using Context source into NGSI-LD: {} ms", timeElapsed);
            response.setData(listConvertedIntoNGSILD);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDContextDataConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDStandardDataConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.NGSILDConverterService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private NGSILDConverterService ngsildConverterService;

    @PostMapping(value = "/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertToNGSILDWithoutContext(
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDStandardDataConfig importConfig) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        if (geoLocationConfig.size() > 2) {
            String errGeoLocMessage = "Somente é permitido um campo para geolocalização. Tamanho atual: {}";
            response.getErrors().add(errGeoLocMessage);
            log.error(errGeoLocMessage, geoLocationConfig.size());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<LinkedHashMap<String, Object>> listNGSILD;
            long startTime = System.currentTimeMillis();
            listNGSILD = ngsildConverterService.standardConverterNGSILD(sgeolInstance, importConfig, layerPath, null);
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
            @RequestBody ImportNSILDContextDataConfig importContextConfig) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        try {
            long startTime = System.currentTimeMillis();
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildConverterService.contextConverterNGSILD(
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

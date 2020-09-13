package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.ImportNSILDMatchingConverterSetup;
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
@RequestMapping("/sync/ngsild-converter")
@CrossOrigin(origins = "*")
public class NGSILDConverterController {

    @Autowired
    private NGSILDConverterService ngsildConverterService;

    @PostMapping(value = "/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> convertMatchingSetupIntoNGSILD(
            @PathVariable String layerPath,
            @RequestBody ImportNSILDMatchingConverterSetup converterSetup) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();

        try {
            long startTime = System.currentTimeMillis();
            List<LinkedHashMap<String, Object>> listConvertedIntoNGSILD = ngsildConverterService.convertIntoNGSILD(
                    converterSetup.getContextLinks(),
                    converterSetup.getMatchingConverterSetup(),
                    converterSetup.getDataCollection(),
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

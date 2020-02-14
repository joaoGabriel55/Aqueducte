package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.pojos.GeoLocationConfig;
import br.imd.aqueducte.models.pojos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.TaskStatusService;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.service.implementation.TaskStatusServiceImpl.STATUS_DONE;
import static br.imd.aqueducte.service.implementation.TaskStatusServiceImpl.STATUS_ERROR;
import static br.imd.aqueducte.utils.PropertiesParams.APP_TOKEN;
import static br.imd.aqueducte.utils.PropertiesParams.USER_TOKEN;

@RestController
@RequestMapping("/sync/importToSgeol")
@CrossOrigin(origins = "*")
public class ImportDataToSGEOLController {

    @Autowired
    private TaskStatusService taskStatusService;

    @SuppressWarnings("rawtypes")
    @PostMapping(value = {"/{layer}", "/{layer}/{taskId}/{taskIndex}"})
    public ResponseEntity<Response<List<String>>> importNGSILDData(@RequestHeader(APP_TOKEN) String appToken,
                                                                   @RequestHeader(USER_TOKEN) String userToken,
                                                                   @PathVariable String taskId,
                                                                   @PathVariable Integer taskIndex,
                                                                   @PathVariable String layer,
                                                                   @RequestBody Map<String, Object> dataNGSILD) {
        Response<List<String>> response = new Response<>();
        try {
            JSONArray jsonArrayNGSILD = new JSONArray((ArrayList) dataNGSILD.get("data_ngsild"));
            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            List<String> jsonArrayResponse = ngsildTreat.importToSGEOL(layer, appToken, userToken, jsonArrayNGSILD);
            response.setData(jsonArrayResponse);
            logInfo("POST /importToSgeol", null);
        } catch (Exception e) {
            response.getErrors().add(e.getLocalizedMessage());
            logError(e.getMessage(), e.getStackTrace());
            taskStatusService.sendTaskStatusProgress(new HashMap<>(), taskId, taskIndex, STATUS_ERROR);
            return ResponseEntity.badRequest().body(response);
        }
        taskStatusService.sendTaskStatusProgress(new HashMap<>(), taskId, taskIndex, STATUS_DONE);
        return ResponseEntity.ok(response);
    }

    // TODO: Add "APP_TOKEN" and "USER_TOKEN"
    // TODO: Task status
    @PostMapping(value = "/file/{layerPath}")
    public ResponseEntity<Response<List<String>>> convertToNGSILDWithoutContextAndImportData(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithoutContextConfig importConfig
    ) {
        Response<List<String>> response = new Response<>();
        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();

        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        if (geoLocationConfig.size() > 2) {
            String errGeoLocMessage = "Somente é permitido um campo para geolocalização. Tamanho atual: {}";
            response.getErrors().add(errGeoLocMessage);
            logError(errGeoLocMessage, geoLocationConfig.size());
            return ResponseEntity.badRequest().body(response);
        }
        try {
            List<LinkedHashMap<String, Object>> listNGSILD = ngsildTreat.convertToEntityNGSILD(importConfig, layerPath, null);
            JSONArray jsonArrayNGSILD = new JSONArray(listNGSILD);
            try {
                List<String> jsonArrayResponse = ngsildTreat.importToSGEOL(layerPath, appToken, userToken, jsonArrayNGSILD);
                response.setData(jsonArrayResponse);
            } catch (Exception e) {
                response.getErrors().add(e.getLocalizedMessage());
                logError(e.getMessage(), e.getStackTrace());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.getErrors().add(e.getLocalizedMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

}

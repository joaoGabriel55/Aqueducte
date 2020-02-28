package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithContextConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.TaskStatusService;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
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
        String taskTitle = "Importação de dados";
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
            //TODO: Get USER_ID
            taskStatusService.sendTaskStatusProgress(new Task(
                    Integer.parseInt(taskId),
                    "abc",
                    taskIndex,
                    taskTitle,
                    TaskType.IMPORT_DATA,
                    TaskStatus.ERROR,
                    e.getLocalizedMessage()
            ), "status-task-import-process");
            return ResponseEntity.badRequest().body(response);
        }
        taskStatusService.sendTaskStatusProgress(new Task(
                Integer.parseInt(taskId),
                "abc",
                taskIndex,
                taskTitle,
                TaskType.IMPORT_DATA,
                TaskStatus.DONE,
                "Importação de dados para camada " + layer
        ), "status-task-import-process");
        return ResponseEntity.ok(response);
    }

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

    @PostMapping(value = "/file/context/{layerPath}")
    public ResponseEntity<Response<List<String>>> convertToNGSILDWithContextAndImportData(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithContextConfig importConfig
    ) {
        Response<List<String>> response = new Response<>();

        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        try {
            long startTime = System.nanoTime();
            List<LinkedHashMap<String, Object>> listNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    importConfig.getContextLink(),
                    importConfig.getMatchingConfigContent(),
                    importConfig.getDataContentForNGSILDConversion(),
                    layerPath
            );
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            logInfo("Time to convert MatchingConfig Into NGSI-LD: {}", timeElapsed);
            JSONArray jsonArrayNGSILD = new JSONArray(listNGSILD);
            try {
                List<String> jsonArrayResponse = ngsildTreat.importToSGEOL(layerPath, appToken, userToken, jsonArrayNGSILD);
                response.setData(jsonArrayResponse);
            } catch (Exception e) {
                response.getErrors().add(e.getLocalizedMessage());
                logError(e.getMessage(), e.getStackTrace());
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
    }

}

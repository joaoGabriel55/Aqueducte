package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.ImportNSILDMatchingConverterSetup;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ImportNGSILDDataService;
import br.imd.aqueducte.services.LoadDataNGSILDByImportSetupService;
import br.imd.aqueducte.services.NGSILDConverterService;
import br.imd.aqueducte.services.TaskStatusService;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import static br.imd.aqueducte.utils.RequestsUtils.*;

@SuppressWarnings("ALL")
@RestController
@Log4j2
@RequestMapping("/sync/import-ngsild-data")
@CrossOrigin(origins = "*")
public class ImportDataController {

    @Autowired
    private ImportNGSILDDataService importNGSILDDataService;

    @Autowired
    private LoadDataNGSILDByImportSetupService<ImportNGSILDDataSetup> importationSetupService;

    @Autowired
    private NGSILDConverterService ngsildConverterService;

    @Autowired
    private TaskStatusService taskStatusService;

    @PostMapping(value = {"/{layer}", "/{layer}/{taskId}"})
    public ResponseEntity<Response<List<String>>> importNGSILDData(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String middlewareInstance,
            @PathVariable String taskId,
            @PathVariable String layer,
            @RequestBody ImportNGSILDDataSetup setup
    ) {
        Response<List<String>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.importationSetupService.loadData(
                    setup, middlewareInstance, null
            );
            if (response.getErrors().size() > 0) {
                log.error(response.getErrors().get(0));
                taskStatusService.sendTaskStatusProgress(
                        taskId,
                        TaskStatus.ERROR,
                        response.getErrors().get(0),
                        "status-task-import-process"
                );
                return ResponseEntity.badRequest().body(response);
            }
            return importData(appToken, userToken, layer, response, taskId, ngsildData);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/file/{layerPath}")
    public ResponseEntity<Response<List<String>>> importNGSILDDataFromFile(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String middlewareInstance,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDMatchingConverterSetup importConfig
    ) {
        Response<List<String>> response = new Response<>();
        try {
            long startTime = System.currentTimeMillis();
            List<LinkedHashMap<String, Object>> listNGSILD = ngsildConverterService.convertIntoNGSILD(
                    importConfig.getContextLinks(),
                    importConfig.getMatchingConverterSetup(),
                    importConfig.getDataCollection(),
                    layerPath
            );
            if (response.getErrors().size() > 0) {
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            JSONArray jsonArrayNGSILD = new JSONArray(listNGSILD);
            try {
                List<String> jsonArrayResponse = importNGSILDDataService.importData(
                        layerPath, appToken, userToken, jsonArrayNGSILD
                );
                response.setData(jsonArrayResponse);
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;
                log.info("POST time elapsed: {} ms", timeElapsed);
                log.info("POST convertToNGSILDWithContextAndImportData");
            } catch (Exception e) {
                response.getErrors().add(e.getLocalizedMessage());
                log.error(e.getMessage(), e.getStackTrace());
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
    }


    private ResponseEntity<Response<List<String>>> importData(
            String appToken,
            String userToken,
            String layer,
            Response<List<String>> response,
            String taskId,
            List<LinkedHashMap<String, Object>> ngsildData
    ) throws Exception {
        if (ngsildData == null || ngsildData.size() == 0) {
            response.getErrors().add("Nothing to import.");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        } else {
            try {
                JSONArray jsonArrayNGSILD = new JSONArray(ngsildData);
                List<String> jsonArrayResponse = importNGSILDDataService.importData(
                        layer, appToken, userToken, jsonArrayNGSILD
                );

                if (jsonArrayResponse != null && jsonArrayResponse.size() == 0) {
                    response.getErrors().add("Nenhuma Entity importada");
                    log.error(response.getErrors().get(0));
                    taskStatusService.sendTaskStatusProgress(
                            taskId,
                            TaskStatus.ERROR,
                            response.getErrors().get(0),
                            "status-task-import-process"
                    );
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }

                response.setData(jsonArrayResponse);
                log.info("POST /importToSgeol - {} entities", jsonArrayResponse.size());
                if (taskId != null && !taskId.equals("")) {
                    taskStatusService.sendTaskStatusProgress(
                            taskId,
                            TaskStatus.DONE,
                            "Layer: " + layer,
                            "status-task-import-process"
                    );
                    log.info("Web Socket sendTaskStatusProgress");
                }
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                response.getErrors().add("Erro interno");
                log.error(response.getErrors().get(0), e.getMessage());
                taskStatusService.sendTaskStatusProgress(
                        taskId,
                        TaskStatus.ERROR,
                        response.getErrors().get(0),
                        "status-task-import-process"
                );
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
    }


}

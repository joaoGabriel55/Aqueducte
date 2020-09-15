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
import java.util.Map;

import static br.imd.aqueducte.utils.RequestsUtils.HASH_CONFIG;

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

    @PostMapping(value = {"/{type}", "/{type}/{taskId}"})
    public ResponseEntity<Response<List<String>>> importNGSILDData(
            @RequestHeader Map<String, String> headers,
            @RequestParam Map<String, String> allParams,
            @PathVariable(required = false) String taskId,
            @PathVariable String type,
            @RequestBody ImportNGSILDDataSetup setup
    ) {
        Response<List<String>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.importationSetupService.loadData(
                    setup, headers.get(HASH_CONFIG)
            );
            return importData(headers, allParams, type, response, taskId, ngsildData);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/file/{type}")
    public ResponseEntity<Response<List<String>>> importNGSILDDataFromFile(
            @RequestHeader Map<String, String> headers,
            @RequestParam Map<String, String> allParams,
            @PathVariable String type,
            @RequestBody ImportNSILDMatchingConverterSetup importConfig
    ) {
        Response<List<String>> response = new Response<>();
        try {
            long startTime = System.currentTimeMillis();
            List<LinkedHashMap<String, Object>> listNGSILD = ngsildConverterService.convertIntoNGSILD(
                    importConfig.getContextLinks(),
                    type,
                    importConfig.getMatchingConverterSetup(),
                    importConfig.getDataCollection()
            );
            if (response.getErrors().size() > 0) {
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            JSONArray jsonArrayNGSILD = new JSONArray(listNGSILD);
            try {
                importNGSILDDataService.importData(
                        type, headers, allParams, jsonArrayNGSILD
                );
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;
                log.info("POST time elapsed: {} ms", timeElapsed);
                log.info("POST /import-ngsild-data using file");
            } catch (Exception e) {
                response.getErrors().add(e.getLocalizedMessage());
                log.error(e.getMessage(), e.getStackTrace());
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
    }


    private ResponseEntity<Response<List<String>>> importData(
            Map<String, String> headers,
            Map<String, String> allParams,
            String type,
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
                importNGSILDDataService.importData(
                        type, headers, allParams, jsonArrayNGSILD
                );
                log.info("POST /import-ngsild-data");
                if (taskId != null && !taskId.equals("")) {
                    taskStatusService.sendTaskStatusProgress(
                            taskId,
                            TaskStatus.DONE,
                            "Type: " + type,
                            "status-task-import-process"
                    );
                    log.info("Web Socket sendTaskStatusProgress");
                }
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                response.getErrors().add(e.getMessage());
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

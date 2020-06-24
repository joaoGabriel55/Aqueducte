package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.dtos.GeoLocationConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithContextConfig;
import br.imd.aqueducte.models.dtos.ImportNSILDDataWithoutContextConfig;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ImportNGSILDDataService;
import br.imd.aqueducte.services.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.services.TaskStatusService;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import br.imd.aqueducte.utils.NGSILDUtils;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

import static br.imd.aqueducte.utils.FormatterUtils.treatPrimaryField;
import static br.imd.aqueducte.utils.RequestsUtils.*;

@SuppressWarnings("ALL")
@RestController
@Log4j2
@RequestMapping("/sync/importToSgeol")
@CrossOrigin(origins = "*")
public class ImportDataToSGEOLController {

    @Autowired
    private ImportNGSILDDataService importNGSILDDataService;

    @Autowired
    private LoadDataNGSILDByImportationSetupService<ImportationSetupWithoutContext> importationSetupStandardService;

    @Autowired
    private LoadDataNGSILDByImportationSetupService<ImportationSetupWithContext> importationSetupContextService;

    @Autowired
    private TaskStatusService taskStatusService;

    @PostMapping(value = {"/{layer}", "/{layer}/{taskId}"})
    public ResponseEntity<Response<List<String>>> importNGSILDData(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String taskId,
            @PathVariable String layer,
            @RequestBody ImportationSetupWithoutContext importationSetup
    ) {
        Response<List<String>> response = new Response<>();
        List<LinkedHashMap<String, Object>> ngsildData = this.importationSetupStandardService.loadData(
                importationSetup, sgeolInstance, null
        );
        updateData(importationSetup.getPrimaryField(), ngsildData, layer, response, sgeolInstance, appToken, userToken);
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
        return importData(sgeolInstance, appToken, userToken, layer, response, taskId, ngsildData);
    }

    @PostMapping(value = {"/{layer}/context", "/{layer}/{taskId}/context"})
    public ResponseEntity<Response<List<String>>> importNGSILDDataContext(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String taskId,
            @PathVariable String layer,
            @RequestBody ImportationSetupWithContext importationSetup
    ) {
        Response<List<String>> response = new Response<>();
        List<LinkedHashMap<String, Object>> ngsildData = this.importationSetupContextService.loadData(
                importationSetup, sgeolInstance, null
        );
        updateData(importationSetup.getPrimaryField(), ngsildData, layer, response, sgeolInstance, appToken, userToken);
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
        return importData(sgeolInstance, appToken, userToken, layer, response, taskId, ngsildData);
    }

    private void updateData(
            String primaryField,
            List<LinkedHashMap<String, Object>> ngsildData,
            String layer,
            Response<List<String>> response,
            String sgeolInstance,
            String appToken,
            String userToken

    ) {
        NGSILDUtils utils = new NGSILDUtils();
        if (primaryField != null && !primaryField.equals("")) {
            int entitiesUpdated = importNGSILDDataService.updateDataAlreadyImported(
                    layer, sgeolInstance, appToken, userToken, ngsildData, treatPrimaryField(primaryField)
            );
            if (entitiesUpdated == 0) {
                response.getErrors().add("Todos os dados foram atualizados. Nada para importar");
            }
        }
    }

    private ResponseEntity<Response<List<String>>> importData(
            String sgeolInstance,
            String appToken,
            String userToken,
            String layer,
            Response<List<String>> response,
            String taskId,
            List<LinkedHashMap<String, Object>> ngsildData
    ) {
        if (ngsildData == null || ngsildData.size() == 0) {
            response.getErrors().add("Nothing to import.");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        } else {
            try {
                JSONArray jsonArrayNGSILD = new JSONArray(ngsildData);
                List<String> jsonArrayResponse = importNGSILDDataService.importData(
                        layer, sgeolInstance, appToken, userToken, jsonArrayNGSILD
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

    @PostMapping(value = "/file/{layerPath}")
    public ResponseEntity<Response<List<String>>> convertToNGSILDWithoutContextAndImportData(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithoutContextConfig importConfig
    ) {
        Response<List<String>> response = new Response<>();
        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();

        List<GeoLocationConfig> geoLocationConfig = importConfig.getGeoLocationConfig();
        if (geoLocationConfig != null && geoLocationConfig.size() > 2) {
            String errGeoLocMessage = "Somente é permitido um campo para geolocalização. Tamanho atual: {}";
            response.getErrors().add(errGeoLocMessage);
            log.error(errGeoLocMessage, geoLocationConfig.size());
            return ResponseEntity.badRequest().body(response);
        }
        try {
            List<LinkedHashMap<String, Object>> listNGSILD = ngsildTreat.convertToEntityNGSILD(
                    sgeolInstance, importConfig, layerPath, null
            );
            long startTime = System.currentTimeMillis();
            updateData(importConfig.getPrimaryField(), listNGSILD, layerPath, response, sgeolInstance, appToken, userToken);
            if (response.getErrors().size() > 0) {
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            JSONArray jsonArrayNGSILD = new JSONArray(listNGSILD);
            try {
                List<String> jsonArrayResponse = importNGSILDDataService.importData(
                        layerPath, sgeolInstance, appToken, userToken, jsonArrayNGSILD
                );
                response.setData(jsonArrayResponse);
                long endTime = System.currentTimeMillis();
                long timeElapsed = endTime - startTime;
                log.info("POST time elapsed: {} ms", timeElapsed);
                log.info("POST convertToNGSILDWithoutContextAndImportData", jsonArrayResponse.size());
            } catch (Exception e) {
                response.getErrors().add(e.getLocalizedMessage());
                log.error(e.getMessage(), e.getStackTrace());
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.getErrors().add(e.getLocalizedMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/file/context/{layerPath}")
    public ResponseEntity<Response<List<String>>> convertToNGSILDWithContextAndImportData(
            @RequestHeader(APP_TOKEN) String appToken,
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @PathVariable String layerPath,
            @RequestBody ImportNSILDDataWithContextConfig importConfig
    ) {
        Response<List<String>> response = new Response<>();
        NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
        try {
            long startTime = System.currentTimeMillis();
            List<LinkedHashMap<String, Object>> listNGSILD = ngsildTreat.matchingWithContextAndConvertToEntityNGSILD(
                    sgeolInstance,
                    importConfig.getContextLinks(),
                    importConfig.getMatchingConfigContent(),
                    importConfig.getDataContentForNGSILDConversion(),
                    layerPath
            );
            updateData(importConfig.getPrimaryField(), listNGSILD, layerPath, response, sgeolInstance, appToken, userToken);
            if (response.getErrors().size() > 0) {
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            JSONArray jsonArrayNGSILD = new JSONArray(listNGSILD);
            try {
                List<String> jsonArrayResponse = importNGSILDDataService.importData(
                        layerPath, sgeolInstance, appToken, userToken, jsonArrayNGSILD
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

}

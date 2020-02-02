package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.TaskStatusService;
import br.imd.aqueducte.treats.NGSILDTreat;
import br.imd.aqueducte.treats.impl.NGSILDTreatImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.service.implementation.TaskStatusServiceImpl.STATUS_DONE;
import static br.imd.aqueducte.service.implementation.TaskStatusServiceImpl.STATUS_ERROR;
import static br.imd.aqueducte.utils.PropertiesParams.*;

@RestController
@RequestMapping("/sync/importToSgeol")
@CrossOrigin(origins = "*")
public class ImportDataToSGEOLController {

    @Autowired
    private TaskStatusService taskStatusService;

    @SuppressWarnings("rawtypes")
    @PostMapping(value = {"/{layer}", "/{taskId}/{taskIndex}"})
    public ResponseEntity<Response<List<String>>> importToSGEOL(@RequestHeader(APP_TOKEN) String appToken,
                                                                @RequestHeader(USER_TOKEN) String userToken,
                                                                @PathVariable String taskId,
                                                                @PathVariable Integer taskIndex,
                                                                @PathVariable String layer,
                                                                @RequestBody Map<String, Object> dataNGSILD) {
        Response<List<String>> response = new Response<>();
        try {
            JSONArray jsonArrayNGSILD = new JSONArray((ArrayList) dataNGSILD.get("data_ngsild"));
            String url = URL_SGEOL + "v2/" + layer;

            NGSILDTreat ngsildTreat = new NGSILDTreatImpl();
            List<String> jsonArrayResponse = ngsildTreat.importToSGEOL(url, appToken, userToken, jsonArrayNGSILD);
            response.setData(jsonArrayResponse);
            logInfo("POST /importToSgeol", null);
        } catch (Exception e) {
            response.getErrors().add(e.getLocalizedMessage());
            logError(e.getMessage(), e.getStackTrace());
            taskStatusService.sendStatusProgress(new HashMap<>(), taskId, taskIndex, STATUS_ERROR);
            return ResponseEntity.badRequest().body(response);
        }
        taskStatusService.sendStatusProgress(new HashMap<>(), taskId, taskIndex, STATUS_DONE);
        return ResponseEntity.ok(response);
    }

}

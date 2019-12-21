package br.imd.aqueducte.hdfs.controller;

import br.imd.aqueducte.models.RelationshipMap;
import br.imd.aqueducte.models.documents.ImportationSetupWithContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.service.implementation.LoadDataNGSILDByImportationSetupWithContextServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.utils.PropertiesParams.URL_HDFS;

@RestController
@RequestMapping("/sync/handleFilesHadoop")
@CrossOrigin(origins = "*")
public class ManagementFilesHadoopController {

    LoadDataNGSILDByImportationSetupService loadDataNGSILDByImportationSetupService;

    ManagementFilesHadoopController() {
        this.loadDataNGSILDByImportationSetupService = new LoadDataNGSILDByImportationSetupWithContextServiceImpl();
    }

    @GetMapping(value = "/fileStatus/{path}")
    public ResponseEntity<Response<Map<String, Object>>> listFileStatusHDFS(@PathVariable String path) {
        Response<Map<String, Object>> response = new Response<>();
        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = URL_HDFS + path + "/?op=LISTSTATUS";
        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(fullUrl, Map.class);
            response.setData(responseEntity.getBody());
            logInfo("fileStatus", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/contentSummary/{path}")
    public ResponseEntity<Response<Map<String, Object>>> contentSummary(@PathVariable String path) {
        Response<Map<String, Object>> response = new Response<>();
        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = URL_HDFS + path + "/?op=GETCONTENTSUMMARY";
        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(fullUrl, Map.class);
            response.setData(responseEntity.getBody());
            logInfo("contentSummary", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/sendFileToHDFS")
    public ResponseEntity<Response<Long>> test(@RequestBody String path) {
        Response<Long> response = new Response<>();
        try {
//            response.setData(fs.getStatus().getCapacity());
            logInfo("test", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/sendDataNGSILDWithContextToHdfs/webservice")
    public ResponseEntity<Response<Map<String, Object>>> sendDataNGSILDWithContextToHdfs(@RequestBody List<ImportationSetupWithContext> importationSetupWithContextList) {
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> callerServicesResponseStatusMap = new HashMap<>();
        List<String> statusResponseHDFS = new ArrayList<>();
        for (ImportationSetupWithContext importationSetupWithContext : importationSetupWithContextList) {
            loadDataNGSILDByImportationSetupService.loadData(importationSetupWithContext);
            // TODO: Send to HDFS

            statusResponseHDFS.add("Data set successfully imported from Importation Setup: " + importationSetupWithContext.getLabel());
        }
        callerServicesResponseStatusMap.put("hdfs_response_status", statusResponseHDFS);

        // Send Relationship Map for Aqueconnect
        loadDataNGSILDByImportationSetupService.sendRelationshipMapForAqueconnect(null);
        callerServicesResponseStatusMap.put("aqueconnect_status", "Good!");

        response.setData(callerServicesResponseStatusMap);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/sendRelationshipMapAqueconnect")
    public ResponseEntity<Response<String>> sendRelationshipMapAqueconnect(@RequestBody List<RelationshipMap> relationshipMap) {
        Response<String> response = new Response<>();

        // TODO: Send to Map Aqueconnect Micro service

        return ResponseEntity.ok(response);
    }


}

package br.imd.aqueducte.hdfsrelationship.controllers;

import br.imd.aqueducte.hdfsrelationship.services.HDFSService;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.dtos.DataSetRelationship;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import br.imd.aqueducte.service.implementation.LoadDataNGSILDByImportSetupWithContextServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static br.imd.aqueducte.utils.PropertiesParams.STATUS_OK;

/**
 * Make communication between Aqueducte and Aqueconnect
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping("/sync/handleFilesHadoop")
@CrossOrigin(origins = "*")
public class ManagementFilesHDFSController {

    private LoadDataNGSILDByImportationSetupService loadDataNGSILDByImportationSetupService;

    ManagementFilesHDFSController() {
        this.loadDataNGSILDByImportationSetupService = new LoadDataNGSILDByImportSetupWithContextServiceImpl();
    }

    @PostMapping(value = "/sendDataNGSILDWithContextToHdfs/webservice/{importSetupName}")
    public ResponseEntity<Response<Map<String, Object>>> sendDataNGSILDWithContextToHdfs(
            @PathVariable String importSetupName,
            @RequestBody List<ImportationSetupWithContext> importationSetupWithContextList) {
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> callerServicesResponseStatusMap = new HashMap<>();
        List<Map<String, String>> statusResponseHDFS = new ArrayList<>();
        for (ImportationSetupWithContext importationSetupWithContext : importationSetupWithContextList) {
            List<LinkedHashMap<String, Object>> dataLoaded = loadDataNGSILDByImportationSetupService.loadData(importationSetupWithContext);

            // Send to Aqueconnect - HDFS
            HDFSService hdfsService = new HDFSService();
            int statusCode = hdfsService.sendDataHDFS(
                    importationSetupWithContext.getIdUser(),
                    importSetupName,
                    importationSetupWithContext.getLayerSelected(),
                    dataLoaded
            );

            if (statusCode != HttpStatus.CREATED.value()) {
                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("data_set_imported_from_importation_setup", importationSetupWithContext.getLabel());
                mapResponse.put("status", statusCode);
                return ResponseEntity.badRequest().body(response);
            }
            // TODO: Get time elapsed
            Map<String, String> mapResponse = new HashMap<>();
            mapResponse.put("data_set_imported_from_importation_setup", importationSetupWithContext.getLabel());
            statusResponseHDFS.add(mapResponse);
        }
        callerServicesResponseStatusMap.put("hdfs_response_status", statusResponseHDFS);
        response.setData(callerServicesResponseStatusMap);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/dataRelationshipAqueconnect")
    public ResponseEntity<Response<Map<String, Object>>> dataRelationshipAqueconnect(
            @RequestBody DataSetRelationship dataSetRelationship) {
        Response<Map<String, Object>> response = new Response<>();
        Map<String, Object> serviceResponseStatusMap = new HashMap<>();

        // Send to Map Aqueconnect Micro service
        int statusCode = loadDataNGSILDByImportationSetupService.makeDataRelationshipAqueconnect(dataSetRelationship);
        serviceResponseStatusMap.put("aqueconnect_response_status", statusCode);
        response.setData(serviceResponseStatusMap);

        if (statusCode != STATUS_OK) {
            return ResponseEntity.badRequest().body(response);
        }
        // TODO: Get time elapsed
        return ResponseEntity.ok(response);
    }
}

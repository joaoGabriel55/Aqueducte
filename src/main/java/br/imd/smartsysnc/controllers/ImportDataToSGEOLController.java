package br.imd.smartsysnc.controllers;

import br.imd.smartsysnc.models.LinkedIdsForRelationship;
import br.imd.smartsysnc.models.response.Response;
import br.imd.smartsysnc.service.LinkedIdsForRelationshipService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sync/importToSgeol")
@CrossOrigin(origins = "*")
public class ImportDataToSGEOLController {

    @Autowired
    private LinkedIdsForRelationshipService linkedIdsForRelationshipService;

    @PostMapping(value = "/{importationSetupId}")
    public ResponseEntity<Response<Map<String, Object>>> importToSGEOL(
            @PathVariable String importationSetupId,
            @RequestBody Map<String, Object> dataNGSILD) {
        Response<Map<String, Object>> response = new Response<>();
        List<LinkedIdsForRelationship> linkedIdsForRelationshipList = linkedIdsForRelationshipService
                .findByIdImportationSetup(importationSetupId);
        JSONArray jsonArray = new JSONArray((ArrayList) dataNGSILD.get("data_ngsild"));
        jsonArray.forEach(elem -> {
            new JSONObject(elem).get("properties");
        });
        response.setData(dataNGSILD);
        return ResponseEntity.ok(response);
    }

}

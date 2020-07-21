package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.JsonDataService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/sync/jsonFlat")
@CrossOrigin(origins = "*")
public class JsonDataController {

    @Autowired
    private JsonDataService jsonDataService;

    /*
     * Json data converter to flat json data.
     * */
    @PostMapping
    public ResponseEntity<Response<Object>> getFlatJSON(@RequestBody Object dataForConversion) {
        Response<Object> response = new Response<>();
        try {
            Object jsonFlatData = jsonDataService.getFlatJSON(dataForConversion);
            response.setData(jsonFlatData);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("POST /jsonFlat");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/arrayKeys")
    public ResponseEntity<Response<List<String>>> retrieveKeysCollectionFromJSON(@RequestBody Map<String, Object> dataForConversion) {

        Response<List<String>> response = new Response<>();
        try {
            List<String> jsonFlatCollection = jsonDataService.getCollectionKeysFromJSON(dataForConversion);
            response.setData(jsonFlatCollection);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("POST /arrayKeys");
        return ResponseEntity.ok(response);
    }

}

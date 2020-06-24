package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.impl.JsonFlatTreatImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/sync/jsonFlat")
@CrossOrigin(origins = "*")
public class JsonFlatConvertController {

    /*
     * Json data converter to flat json data.
     * */
    @PostMapping
    public ResponseEntity<Response<Object>> getFlatJSON(@RequestBody Object dataForConversion) {
        Response<Object> response = new Response<>();
        JsonFlatTreatImpl jsonFlatTreatImpl = new JsonFlatTreatImpl();
        try {
            Object jsonFlatData = jsonFlatTreatImpl.getFlatJSON(dataForConversion);
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
    public ResponseEntity<Response<List<String>>> getKeysCollectionFromJSON(@RequestBody Map<String, Object> dataForConversion) {

        Response<List<String>> response = new Response<>();
        JsonFlatTreatImpl jsonFlatTreatImpl = new JsonFlatTreatImpl();
        try {
            List<String> jsonFlatCollection = jsonFlatTreatImpl.getKeysCollectionFromJSON(dataForConversion);
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

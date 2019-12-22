package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.JsonFlatConvertTreat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@RestController
@RequestMapping("/sync/jsonFlat")
@CrossOrigin(origins = "*")
public class JsonFlatConvertController {

    /*
     * Json data converter to flat json data.
     * */
    @PostMapping
    public ResponseEntity<Response<Object>> jsonFlatConvert(@RequestBody Object dataForConversion) {

        Response<Object> response = new Response<>();
        JsonFlatConvertTreat jsonFlatConvertTreat = new JsonFlatConvertTreat();
        try {
            Object jsonFlatData = jsonFlatConvertTreat.getJsonFlat(dataForConversion);
            response.setData(jsonFlatData);
            logInfo("POST /jsonFlat", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/arrayKeys")
    public ResponseEntity<Response<List<String>>> getKeysFromArrayFields(@RequestBody Map<String, Object> dataForConversion) {

        Response<List<String>> response = new Response<>();
        JsonFlatConvertTreat jsonFlatConvertTreat = new JsonFlatConvertTreat();
        try {
            List<String> jsonFlatCollection = jsonFlatConvertTreat.getArrayKeysFromJsonData(dataForConversion);
            response.setData(jsonFlatCollection);
            logInfo("POST /arrayKeys", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

}

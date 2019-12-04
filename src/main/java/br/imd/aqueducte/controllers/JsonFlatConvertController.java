package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.treats.JsonFlatConvertTreat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Response<List<Object>>> jsonFlatConvert(@RequestBody List<Object> dataForConversion) {

        Response<List<Object>> response = new Response<>();
        JsonFlatConvertTreat jsonFlatConvertTreat = new JsonFlatConvertTreat();
        try {
            List<Object> jsonFlatCollection = jsonFlatConvertTreat.getJsonFlatCollection(dataForConversion);
            response.setData(jsonFlatCollection);
            logInfo("POST /jsonFlat", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

}

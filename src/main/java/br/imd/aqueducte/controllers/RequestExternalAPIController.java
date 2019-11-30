package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.utils.RequestsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/sync/requestExternalAPI")
@CrossOrigin(origins = "*")
public class RequestExternalAPIController {

    /*
     * Payload example:
     *
     * {
     *    "method": "HTTP Verb",
     *    "headers": {"key": "value"},
     *    "url": "url",
     *    "params": {"key": "value"},
     *    "data": {...}
     * }
     *
     * */
    @PostMapping
    public ResponseEntity<Response<Map<String, Object>>> requestExternalAPI(@RequestBody Map<Object, Object> paramsRequest) {
        RequestsUtils requestsUtils = new RequestsUtils();
        Response<Map<String, Object>> response = new Response<>();
        try {
            response.setData(requestsUtils.requestToAPI(paramsRequest));
        } catch (IOException e) {
            response.getErrors().add("Error at retrieve data");
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

}

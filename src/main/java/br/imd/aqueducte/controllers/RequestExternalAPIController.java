package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.utils.RequestsUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@Log4j2
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
            log.error("Error at retrieve data");
            e.printStackTrace();
        }

        log.info("POST/ Request external API");
        return ResponseEntity.ok(response);
    }

}

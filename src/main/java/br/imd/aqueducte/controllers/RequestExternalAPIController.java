package br.imd.aqueducte.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.utils.RequestsUtils;

@RestController
@RequestMapping("/sync/requestExternalAPI")
@CrossOrigin(origins = "*")
public class RequestExternalAPIController {

    @PostMapping
    public ResponseEntity<Response<Map<String, Object>>> requestExternalAPI(@RequestBody Map<Object, Object> paramsRequest) {

        Response<Map<String, Object>> response = new Response<>();
        try {
            response.setData(RequestsUtils.requestToAPI(paramsRequest));
        } catch (IOException e) {
            response.getErrors().add("Error at retrieve data");
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

}

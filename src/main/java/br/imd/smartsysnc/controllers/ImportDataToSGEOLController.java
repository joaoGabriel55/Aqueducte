package br.imd.smartsysnc.controllers;

import br.imd.smartsysnc.filters.RequestResponseLoggingFilter;
import br.imd.smartsysnc.models.response.Response;
import br.imd.smartsysnc.service.LinkedIdsForRelationshipService;
import br.imd.smartsysnc.utils.RequestsUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/sync/importToSgeol")
@CrossOrigin(origins = "*")
public class ImportDataToSGEOLController {

    @Autowired
    private LinkedIdsForRelationshipService linkedIdsForRelationshipService;

    @PostMapping(value = "/{layer}")
    public ResponseEntity<Response<Map<String, Object>>> importToSGEOL(
            @RequestHeader(RequestResponseLoggingFilter.APP_TOKEN) String appToken,
            @RequestHeader(RequestResponseLoggingFilter.USER_TOKEN) String userToken,
            @PathVariable String layer,
            @RequestBody Map<String, Object> dataNGSILD) {
        Response<Map<String, Object>> response = new Response<>();
        try {
            JSONArray jsonArray = new JSONArray((ArrayList) dataNGSILD.get("data_ngsild"));
            response.setData(dataNGSILD);

            String url = RequestsUtils.URL_SGEOL + layer;

            // create headers
            HttpHeaders headers = new HttpHeaders();
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            // set custom header
            headers.set(RequestResponseLoggingFilter.APP_TOKEN, appToken);
            headers.set(RequestResponseLoggingFilter.USER_TOKEN, userToken);
            // build the request
            HttpEntity request = new HttpEntity(headers);


            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            // use `exchange` method for HTTP call

            for (int i = 0; i < jsonArray.length(); i++) {
                HttpEntity<String> entity = new HttpEntity<>(jsonArray.get(i).toString(), headers);
                ResponseEntity<String> responseSGEOL = restTemplate.exchange(url, HttpMethod.POST, entity, String.class, 1);
                if (responseSGEOL.getStatusCode() == HttpStatus.OK) {
                    responseSGEOL.getBody();
                }
            }
        } catch (Exception e) {
            response.getErrors().add(e.getLocalizedMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

}

package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.utils.RequestsUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/sync/contextSources")
@CrossOrigin(origins = "*")
public class ContextSourcesLoadController {

    @PostMapping
    public ResponseEntity<Response<Object>> loadContexts(@RequestBody Map<String, String> urlContextSource) {
        RequestsUtils requestsUtils = new RequestsUtils();

        Response<Object> response = new Response<>();
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(urlContextSource.get("url"));
            HttpResponse responseHttp = httpClient.execute(request);
            ObjectMapper mapper = new ObjectMapper();
            Object jsonData = mapper.readValue(requestsUtils.readBodyReq(responseHttp.getEntity().getContent()), Object.class);
            response.setData(jsonData);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

}

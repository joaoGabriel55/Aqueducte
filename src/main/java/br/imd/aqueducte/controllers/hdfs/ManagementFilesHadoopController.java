package br.imd.aqueducte.controllers.hdfs;

import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.utils.RequestsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static br.imd.aqueducte.utils.RequestsUtils.URL_HDFS;

@RestController
@RequestMapping("/sync/handleFilesHadoop")
@CrossOrigin(origins = "*")
public class ManagementFilesHadoopController {

    @GetMapping(value = "/fileStatus/{path}")
    public ResponseEntity<Response<Map<String, Object>>> listFileStatusHDFS(@PathVariable String path) {
        Response<Map<String, Object>> response = new Response<>();
        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = URL_HDFS + path + "/?op=LISTSTATUS";
        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(fullUrl, Map.class);
            response.setData(responseEntity.getBody());
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/contentSummary/{path}")
    public ResponseEntity<Response<Map<String, Object>>> contentSummary(@PathVariable String path) {
        Response<Map<String, Object>> response = new Response<>();
        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = URL_HDFS + path + "/?op=GETCONTENTSUMMARY";
        try {
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(fullUrl, Map.class);
            response.setData(responseEntity.getBody());
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }


}

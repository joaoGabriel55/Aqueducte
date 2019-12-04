package br.imd.aqueducte.controllers.hdfs;

import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.response.Response;
import org.apache.hadoop.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;
import static br.imd.aqueducte.utils.PropertiesParams.URL_HDFS;

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
            logInfo("fileStatus", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
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
            logInfo("contentSummary", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }


    @PutMapping(value = "/sendFileToHDFS")
    public ResponseEntity<Response<Long>> test(@RequestBody String path) {
        Response<Long> response = new Response<>();
        try {
//            response.setData(fs.getStatus().getCapacity());
            logInfo("test", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok().body(response);
    }


}

package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.mongodocuments.external_app_config.ExternalAppConfig;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ExternalAppConfigService;
import com.mongodb.DuplicateKeyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequestMapping("/sync/external-app-config")
@CrossOrigin(origins = "*")
public class ExternalAppConfigController {

    @Autowired
    private ExternalAppConfigService service;

    @GetMapping("/{hashConfig}")
    public ResponseEntity<Response<ExternalAppConfig>> getConfigByHash(@PathVariable String hashConfig) {
        Response<ExternalAppConfig> response = new Response<>();
        try {
            ExternalAppConfig config = service.getConfigByHash(hashConfig);
            if (config == null) {
                response.getErrors().add("ExternalAppConfig " + hashConfig + " not found");
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(config);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        log.info("GET ExternalAppConfig by hash - {}", hashConfig);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ExternalAppConfig>> create(@RequestBody ExternalAppConfig config) {
        Response<ExternalAppConfig> response = new Response<>();
        try {
            if (config.getId() == null) {
                ExternalAppConfig result = service.createOrUpdate(config);
                response.setData(result);
            } else {
                response.getErrors().add("Object inconsistent");
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
        } catch (DuplicateKeyException e) {
            response.getErrors().add("Duplicate ID");
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        log.info("POST ExternalAppConfig");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{hashConfig}")
    public ResponseEntity<Response<ExternalAppConfig>> update(
            @PathVariable String hashConfig,
            @RequestBody ExternalAppConfig config
    ) {
        Response<ExternalAppConfig> response = new Response<>();
        try {
            ExternalAppConfig configFound = service.getConfigByHash(hashConfig);
            if (configFound != null) {
                config.setId(configFound.getId());
                config.setHashConfig(configFound.getHashConfig());
                config.setCreatedDate(configFound.getCreatedDate());
                ExternalAppConfig result = service.createOrUpdate(config);
                response.setData(result);
            }
        } catch (DuplicateKeyException e) {
            response.getErrors().add("Duplicate ID");
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("PATCH ExternalAppConfig - hash: {}", hashConfig);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{hashConfig}")
    public ResponseEntity<Response<String>> delete(@PathVariable String hashConfig) {
        Response<String> response = new Response<>();
        try {
            service.deleteByHash(hashConfig);
            log.info("DELETE ExternalAppConfig - hash: {}", hashConfig);
            response.setData(hashConfig);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

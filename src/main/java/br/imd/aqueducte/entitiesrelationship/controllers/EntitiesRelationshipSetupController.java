package br.imd.aqueducte.entitiesrelationship.controllers;

import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.entitiesrelationship.business.EntitiesRelationshipSetupValidate;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipSetupService;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.models.response.Response;
import com.mongodb.DuplicateKeyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Log4j2
@RequestMapping("/sync/entitiesRelationshipSetup")
@CrossOrigin(origins = "*")
public class EntitiesRelationshipSetupController extends GenericController {

    @Autowired
    private EntitiesRelationshipSetupService service;

    @Autowired
    private EntitiesRelationshipSetupValidate validator;

    @GetMapping
    public ResponseEntity<Response<List<EntitiesRelationshipSetup>>> getAll() {
        Response<List<EntitiesRelationshipSetup>> response = new Response<>();
        try {
            List<EntitiesRelationshipSetup> entitiesRelationshipSetups = service.findAll();
            response.setData(entitiesRelationshipSetups);
            log.info("GET getAll EntitiesRelationshipSetup");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EntitiesRelationshipSetup>> getById(@PathVariable String id) {
        Response<EntitiesRelationshipSetup> response = new Response<>();
        try {
            Optional<EntitiesRelationshipSetup> setupFound = service.findById(id);
            if (setupFound.isEmpty()) {
                response.getErrors().add("EntitiesRelationshipSetup " + id + " not found");
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(setupFound.get());
            log.info("GET getById EntitiesRelationshipSetup {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Response<List<EntitiesRelationshipSetup>>> getByStatus(
            @RequestParam("status") String status) {
        Response<List<EntitiesRelationshipSetup>> response = new Response<>();
        if (!validator.validateStatusParam(status)) {
            response.getErrors().add("Status not exists");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        try {
            List<EntitiesRelationshipSetup> entitiesRelationshipSetups = service.findByStatus(status);
            response.setData(entitiesRelationshipSetups);
            log.info("GET getByStatus EntitiesRelationshipSetup - {}", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Response<EntitiesRelationshipSetup>> save(
            HttpServletRequest request,
            @RequestBody EntitiesRelationshipSetup entitiesRelationshipSetup) {
        Response<EntitiesRelationshipSetup> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        List<String> validatorMessage = validator.validateEntitiesRelationshipSetup(entitiesRelationshipSetup);
        if (validatorMessage.size() != 0 && !entitiesRelationshipSetup.getStatus().equals(EntitiesRelationshipSetupStatus.PENDING)) {
            response.getErrors().addAll(validatorMessage);
            log.error(response.getErrors().toString());
            return ResponseEntity.badRequest().body(response);
        }

        entitiesRelationshipSetup.setIdUser(idUser);
        try {
            entitiesRelationshipSetup.setDateCreated(new Date());
            entitiesRelationshipSetup.setDateModified(new Date());
            EntitiesRelationshipSetup entitiesRelationshipSetups = service.createOrUpdate(entitiesRelationshipSetup);
            response.setData(entitiesRelationshipSetups);
            log.info("POST save EntitiesRelationshipSetup");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Response<EntitiesRelationshipSetup>> update(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody EntitiesRelationshipSetup entitiesRelationshipSetup) {
        Response<EntitiesRelationshipSetup> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        } else if (!entitiesRelationshipSetup.getId().equals(id)) {
            response.getErrors().add("Id from payload does not match with id from URL path");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        List<String> validatorMessage = validator.validateEntitiesRelationshipSetup(entitiesRelationshipSetup);
        if (validatorMessage.size() != 0 && !entitiesRelationshipSetup.getStatus().equals(EntitiesRelationshipSetupStatus.PENDING)) {
            response.getErrors().addAll(validatorMessage);
            log.error(response.getErrors().toString());
            return ResponseEntity.badRequest().body(response);
        }
        try {
            Optional<EntitiesRelationshipSetup> entitiesRelationshipSetupFound = service.findById(id);
            if (entitiesRelationshipSetupFound.isPresent()) {
                entitiesRelationshipSetup.setDateCreated(entitiesRelationshipSetupFound.get().getDateCreated());
                entitiesRelationshipSetup.setDateModified(new Date());
                service.createOrUpdate(entitiesRelationshipSetup);
                response.setData(entitiesRelationshipSetup);
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
        log.info("PATCH update EntitiesRelationshipSetup - {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable String id) {
        Response<String> response = new Response<>();
        try {
            String entitiesRelationshipSetupDeletedId = service.delete(id);
            response.setData(entitiesRelationshipSetupDeletedId);
            log.info("DELETE delete EntitiesRelationshipSetup - {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}

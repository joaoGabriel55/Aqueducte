package br.imd.aqueducte.entitiesrelationship.controllers;

import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.entitiesrelationship.business.EntitiesRelationshipSetupValidate;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipSetupService;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.models.response.Response;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@RestController
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
            logInfo("GET getAll EntitiesRelationshipSetup", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            logError(response.getErrors().get(0), e.getMessage());
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
                logError(response.getErrors().get(0), null);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(setupFound.get());
            logInfo("GET getById EntitiesRelationshipSetup", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Response<List<EntitiesRelationshipSetup>>> getByStatus(
            @RequestParam("status") String status) {
        Response<List<EntitiesRelationshipSetup>> response = new Response<>();
        if (!validator.validateStatusParam(status)) {
            response.getErrors().add("Status not exists");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }
        try {
            List<EntitiesRelationshipSetup> entitiesRelationshipSetups = service.findByStatus(status);
            response.setData(entitiesRelationshipSetups);
            logInfo("GET getByStatus EntitiesRelationshipSetup", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            logError(response.getErrors().get(0), e.getMessage());
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
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }
        List<String> validatorMessage = validator.validateEntitiesRelationshipSetup(entitiesRelationshipSetup);
        if (validatorMessage.size() != 0 && !entitiesRelationshipSetup.getStatus().equals(EntitiesRelationshipSetupStatus.PENDING)) {
            response.getErrors().addAll(validatorMessage);
            logError(response.getErrors().toString(), null);
            return ResponseEntity.badRequest().body(response);
        }

        entitiesRelationshipSetup.setIdUser(idUser);
        try {
            entitiesRelationshipSetup.setDateCreated(new Date());
            entitiesRelationshipSetup.setDateModified(new Date());
            EntitiesRelationshipSetup entitiesRelationshipSetups = service.createOrUpdate(entitiesRelationshipSetup);
            response.setData(entitiesRelationshipSetups);
            logInfo("POST save EntitiesRelationshipSetup", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            logError(response.getErrors().get(0), e.getMessage());
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
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        } else if (!entitiesRelationshipSetup.getId().equals(id)) {
            response.getErrors().add("Id from payload does not match with id from URL path");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }
        List<String> validatorMessage = validator.validateEntitiesRelationshipSetup(entitiesRelationshipSetup);
        if (validatorMessage.size() != 0 && !entitiesRelationshipSetup.getStatus().equals(EntitiesRelationshipSetupStatus.PENDING)) {
            response.getErrors().addAll(validatorMessage);
            logError(response.getErrors().toString(), null);
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
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        logInfo("PATCH update EntitiesRelationshipSetup", null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> delete(@PathVariable String id) {
        Response<String> response = new Response<>();
        try {
            String entitiesRelationshipSetupDeletedId = service.delete(id);
            response.setData(entitiesRelationshipSetupDeletedId);
            logInfo("DELETE delete EntitiesRelationshipSetup", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getErrors().add(e.getLocalizedMessage());
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}

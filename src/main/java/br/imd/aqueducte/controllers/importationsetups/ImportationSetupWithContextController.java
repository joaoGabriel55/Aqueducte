package br.imd.aqueducte.controllers.importationsetups;


import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.MatchingConfig;
import br.imd.aqueducte.models.documents.ImportationSetupWithContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.ImportationSetupWithContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@RestController
@RequestMapping("/sync/withContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupWithContextController extends GenericController {

    @Autowired
    private ImportationSetupWithContextService importationSetupWithContextService;

    @GetMapping(value = "{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupWithContext>>> findAllImportationSetupWithoutContext(
            @PathVariable("page") int page, @PathVariable("count") int count) {
        Response<Page<ImportationSetupWithContext>> response = new Response<>();
        try {
            Page<ImportationSetupWithContext> importationSetupWithContextList = importationSetupWithContextService.findAllPageable(page, count);
            response.setData(importationSetupWithContextList);
            logInfo("fileStatus", null);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.getErrors().add("Error on save Importation Setup with Context");
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithContext>> getByIdImportationSetupWithContext(
            @PathVariable(required = true) String id) {

        Response<ImportationSetupWithContext> response = new Response<>();
        try {
            Optional<ImportationSetupWithContext> importationSetupWithContext = importationSetupWithContextService
                    .findById(id);
            response.setData(importationSetupWithContext.get());
            logInfo("GET Importation Setup With Context", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getStackTrace());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ImportationSetupWithContext>> saveImportationSetupWithContext(
            @ModelAttribute("user-id") String userId, @RequestBody ImportationSetupWithContext importationSetupWithContext) {
        Response<ImportationSetupWithContext> response = new Response<>();
        try {
            List<MatchingConfig> matchingConfigList = importationSetupWithContext.getMatchingConfigList().stream().map(elem -> {
                if (elem.getLocationToGeoJsonConfig().size() > 0) elem.setLocation(true);
                return elem;
            }).collect(Collectors.toList());
            importationSetupWithContext.setMatchingConfigList(matchingConfigList);

            if (importationSetupWithContext.getIdUser() == null && importationSetupWithContext.getId() == null) {
                importationSetupWithContext.setIdUser(userId);
                importationSetupWithContext.setDateCreated(new Date());
                importationSetupWithContext.setDateModified(new Date());
            } else if (importationSetupWithContext.getId() != null) {
                Optional<ImportationSetupWithContext> importationSetupWithContextUpdated = importationSetupWithContextService.findById(importationSetupWithContext.getId());
                importationSetupWithContext.setDateCreated(importationSetupWithContextUpdated.get().getDateCreated());
                importationSetupWithContext.setDateModified(new Date());
            }

            ImportationSetupWithContext importationSetupWithContextRes = importationSetupWithContextService.createOrUpdate(importationSetupWithContext);
            response.setData(importationSetupWithContextRes);
            logInfo("ImportationSetupWithContext: {}", importationSetupWithContextRes.toString());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.getErrors().add("Error on save Importation Setup with Context");
            logError("Error on save Importation Setup with Context", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> deleteImportationSetupWithContext(
            @PathVariable(required = true) String id) {
        Response<String> response = new Response<>();
        try {

            String idDeleted = importationSetupWithContextService.delete(id);
            if (idDeleted != null)
                response.setData(idDeleted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

}

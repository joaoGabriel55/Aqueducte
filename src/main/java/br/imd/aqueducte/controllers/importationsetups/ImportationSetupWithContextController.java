package br.imd.aqueducte.controllers.importationsetups;


import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ImportationSetupWithContextService;
import br.imd.aqueducte.services.LoadDataNGSILDByImportationSetupService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@SuppressWarnings("ALL")
@RestController
@RequestMapping("/sync/withContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupWithContextController extends GenericController {

    @Autowired
    private ImportationSetupWithContextService importationSetupWithContextService;

    @Autowired
    private LoadDataNGSILDByImportationSetupService<ImportationSetupWithContext> loadDataNGSILDByImportationSetupService;

    @GetMapping(value = "/{importType}/{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupWithContext>>> findAllImportationSetupWithoutContext(
            @PathVariable("importType") String importType, @PathVariable("page") int page, @PathVariable("count") int count
    ) {
        Response<Page<ImportationSetupWithContext>> response = new Response<>();
        try {
            Page<ImportationSetupWithContext> importationSetupWithContextList = importationSetupWithContextService
                    .findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
                            importType.toUpperCase(), page, count
                    );
            if (!importationSetupWithContextList.hasContent()) {
                response.getErrors().add("Has not content");
                return ResponseEntity.badRequest().body(response);
            }

            response.setData(importationSetupWithContextList);
            logInfo("GET findAllImportationSetupWithoutContext", null);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.getErrors().add("Error on get Importation Setup with Context list");
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithContext>> getByIdImportationSetupWithContext(
            @PathVariable String id) {

        Response<ImportationSetupWithContext> response = new Response<>();
        try {
            Optional<ImportationSetupWithContext> importationSetupWithContext = importationSetupWithContextService
                    .findById(id);
            importationSetupWithContext.ifPresent(response::setData);
            logInfo("GET Importation Setup With Context", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Response<List<ImportationSetupWithContext>>> findImportationSetupWithContextByFilePathAndUserId(
            @PathVariable("userId") String userId,
            @RequestParam("filePath") String filePath
    ) {
        Response<List<ImportationSetupWithContext>> response = new Response<>();

        if (filePath == null || filePath.equals("")) {
            response.getErrors().add("File path must be informed");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<ImportationSetupWithContext> importationSetupWithContextList = importationSetupWithContextService.findByUserIdAndFilePath(userId, filePath);
            response.setData(importationSetupWithContextList);
            logInfo("GET findImportationSetupWithContextByFilePathAndUserId", null);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(e.getMessage(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ImportationSetupWithContext>> saveImportationSetupWithContext(
            HttpServletRequest request,
            @RequestBody ImportationSetupWithContext importationSetupWithContext) {
        Response<ImportationSetupWithContext> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }
        importationSetupWithContext.setIdUser(idUser);
        try {
            if (importationSetupWithContext.getId() == null) {
                importationSetupWithContext.setDateCreated(new Date());
                importationSetupWithContext.setDateModified(new Date());
                importationSetupWithContextService.createOrUpdate(importationSetupWithContext);
                response.setData(importationSetupWithContext);
                logInfo("POST saveImportationSetupWithContext", null);
            } else {
                response.getErrors().add("Object inconsistent");
                logError(response.getErrors().get(0), null);
                return ResponseEntity.badRequest().body(response);
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
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithContext>> updateImportationSetupWithContext(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody ImportationSetupWithContext importationSetupWithoutContext
    ) {
        Response<ImportationSetupWithContext> response = new Response<>();

        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        } else if (!importationSetupWithoutContext.getId().equals(id)) {
            response.getErrors().add("Id from payload does not match with id from URL path");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<ImportationSetupWithContext> importSetup = importationSetupWithContextService.findById(id);
            if (importSetup.isPresent()) {
                importationSetupWithoutContext.setDateCreated(importSetup.get().getDateCreated());
                importationSetupWithoutContext.setDateModified(new Date());
                importationSetupWithContextService.createOrUpdate(importationSetupWithoutContext);
                response.setData(importationSetupWithoutContext);
                logInfo("PUT updateImportationSetupWithContext", null);
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
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> deleteImportationSetupWithContext(
            @PathVariable(required = true) String id
    ) {
        Response<String> response = new Response<>();
        try {
            String idDeleted = importationSetupWithContextService.delete(id);
            if (idDeleted != null) {
                response.setData(idDeleted);
                logInfo("DELETE deleteImportationSetupWithContext", null);
            }
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/load-ngsild-data")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> loadNGSILDDataFromImportSetupWithContext(
            @RequestParam boolean samples,
            @RequestBody ImportationSetupWithContext importationSetupWithContext
    ) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.loadDataNGSILDByImportationSetupService.loadData(
                    importationSetupWithContext, ""
            );
            response.setData(samples ? getSamples(ngsildData) : ngsildData);
            logInfo("POST loadNGSILDDataFromImportSetupWithContext", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/load-ngsild-data/count")
    public ResponseEntity<Response<Integer>> loadCountNGSILDDataFromImportSetupWithoutContext(
            @RequestBody ImportationSetupWithContext importationSetupWithContext
    ) {
        Response<Integer> response = new Response<>();
        try {
            response.setData(
                    this.loadDataNGSILDByImportationSetupService
                            .loadData(importationSetupWithContext, "")
                            .size()
            );
            logInfo("POST loadCountNGSILDDataFromImportSetupWithoutContext", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}

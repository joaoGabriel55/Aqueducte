package br.imd.aqueducte.controllers.importationsetups;


import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ImportationSetupContextService;
import br.imd.aqueducte.services.LoadDataNGSILDByImportationSetupService;
import com.mongodb.DuplicateKeyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static br.imd.aqueducte.utils.RequestsUtils.SGEOL_INSTANCE;
import static br.imd.aqueducte.utils.RequestsUtils.USER_TOKEN;

@SuppressWarnings("ALL")
@RestController
@Log4j2
@RequestMapping("/sync/withContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupContextController extends GenericController {

    @Autowired
    private ImportationSetupContextService importationSetupContextService;

    @Autowired
    private LoadDataNGSILDByImportationSetupService<ImportationSetupContext> loadDataNGSILDByImportationSetupService;

    @GetMapping(value = "/{idUser}/{importType}/{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupContext>>> findAllImportationSetupWithoutContextByUserId(
            @PathVariable("idUser") String idUser,
            @PathVariable("importType") String importType,
            @PathVariable("page") int page,
            @PathVariable("count") int count
    ) {
        Response<Page<ImportationSetupContext>> response = new Response<>();
        try {
            Page<ImportationSetupContext> importationSetupWithContextList = importationSetupContextService
                    .findByIdUserAndImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
                            idUser, importType.toUpperCase(), page, count
                    );
            if (!importationSetupWithContextList.hasContent()) {
                response.getErrors().add("Has not content");
                return ResponseEntity.badRequest().body(response);
            }

            response.setData(importationSetupWithContextList);
            log.info("GET findAllImportationSetupWithoutContext");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.getErrors().add("Error on get Importation Setup with Context list");
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupContext>> getByIdImportationSetupWithContext(
            @PathVariable String id) {

        Response<ImportationSetupContext> response = new Response<>();
        try {
            Optional<ImportationSetupContext> importationSetupWithContext = importationSetupContextService
                    .findById(id);
            importationSetupWithContext.ifPresent(response::setData);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET Importation Setup With Context");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Response<List<ImportationSetupContext>>> findImportationSetupWithContextByFilePathAndUserId(
            @PathVariable("userId") String userId,
            @RequestParam("filePath") String filePath
    ) {
        Response<List<ImportationSetupContext>> response = new Response<>();

        if (filePath == null || filePath.equals("")) {
            response.getErrors().add("File path must be informed");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<ImportationSetupContext> importationSetupContextList = importationSetupContextService.findByUserIdAndFilePath(userId, filePath);
            response.setData(importationSetupContextList);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET findImportationSetupWithContextByFilePathAndUserId");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ImportationSetupContext>> saveImportationSetupWithContext(
            HttpServletRequest request,
            @RequestBody ImportationSetupContext importationSetupContext) {
        Response<ImportationSetupContext> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        importationSetupContext.setIdUser(idUser);
        try {
            if (importationSetupContext.getId() == null) {
                importationSetupContext.setDateCreated(new Date());
                importationSetupContext.setDateModified(new Date());
                importationSetupContextService.createOrUpdate(importationSetupContext);
                response.setData(importationSetupContext);
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
            return ResponseEntity.badRequest().body(response);
        }
        log.info("POST saveImportationSetupWithContext");
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupContext>> updateImportationSetupWithContext(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody ImportationSetupContext importationSetupWithoutContext
    ) {
        Response<ImportationSetupContext> response = new Response<>();

        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        } else if (!importationSetupWithoutContext.getId().equals(id)) {
            response.getErrors().add("Id from payload does not match with id from URL path");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Optional<ImportationSetupContext> importSetup = importationSetupContextService.findById(id);
            if (importSetup.isPresent()) {
                importationSetupWithoutContext.setDateCreated(importSetup.get().getDateCreated());
                importationSetupWithoutContext.setDateModified(new Date());
                importationSetupContextService.createOrUpdate(importationSetupWithoutContext);
                response.setData(importationSetupWithoutContext);
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
        log.info("PUT updateImportationSetupWithContext");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> deleteImportationSetupWithContext(
            @PathVariable(required = true) String id
    ) {
        Response<String> response = new Response<>();
        try {
            String idDeleted = importationSetupContextService.delete(id);
            if (idDeleted != null) {
                response.setData(idDeleted);
            }
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("DELETE deleteImportationSetupWithContext");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/load-ngsild-data")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> loadNGSILDDataFromImportSetupWithContext(
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @RequestParam boolean samples,
            @RequestBody ImportationSetupContext importationSetupContext
    ) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.loadDataNGSILDByImportationSetupService.loadData(
                    importationSetupContext, sgeolInstance, userToken
            );
            response.setData(samples ? getSamples(ngsildData) : ngsildData);
            log.info("POST loadNGSILDDataFromImportSetupWithContext");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/load-ngsild-data/count")
    public ResponseEntity<Response<Integer>> loadCountNGSILDDataFromImportSetupWithoutContext(
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @RequestBody ImportationSetupContext importationSetupContext
    ) {
        Response<Integer> response = new Response<>();
        try {
            response.setData(this.loadDataNGSILDByImportationSetupService
                    .loadData(importationSetupContext, sgeolInstance, userToken)
                    .size());
            log.info("POST loadCountNGSILDDataFromImportSetupWithoutContext");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}

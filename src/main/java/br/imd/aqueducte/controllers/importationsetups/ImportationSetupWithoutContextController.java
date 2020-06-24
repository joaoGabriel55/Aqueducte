package br.imd.aqueducte.controllers.importationsetups;

import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ImportationSetupWithoutContextService;
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
@RequestMapping("/sync/withoutContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupWithoutContextController extends GenericController {

    @Autowired
    private ImportationSetupWithoutContextService impSetupWithoutCxtService;

    @Autowired
    private LoadDataNGSILDByImportationSetupService<ImportationSetupWithoutContext> loadDataNGSILDByImportationSetupService;

    @GetMapping(value = "/{idUser}/{importType}/{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupWithoutContext>>> findImportTypeImportationSetupWithoutContext(
            @PathVariable("idUser") String idUser,
            @PathVariable("importType") String importType,
            @PathVariable("page") int page,
            @PathVariable("count") int count
    ) {
        Response<Page<ImportationSetupWithoutContext>> response = new Response<>();
        try {
            Page<ImportationSetupWithoutContext> list = impSetupWithoutCxtService
                    .findByIdUserImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
                            idUser, importType.toUpperCase(), page, count
                    );
            if (!list.hasContent()) {
                response.getErrors().add("Has not content");
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(list);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET findImportTypeImportationSetupWithoutContext");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithoutContext>> getByIdImportationSetupWithoutContext(
            @PathVariable String id
    ) {
        Response<ImportationSetupWithoutContext> response = new Response<>();
        try {
            Optional<ImportationSetupWithoutContext> importationSetupWithoutContext = impSetupWithoutCxtService
                    .findById(id);
            response.setData(importationSetupWithoutContext.get());
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET getByIdImportationSetupWithoutContext");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Response<List<ImportationSetupWithoutContext>>> findImportationSetupWithoutContextByFilePathAndUserId(
            @PathVariable("userId") String userId,
            @RequestParam("filePath") String filePath
    ) {
        Response<List<ImportationSetupWithoutContext>> response = new Response<>();

        if (filePath == "" || filePath == null) {
            response.getErrors().add("File path must be informed");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }

        try {
            List<ImportationSetupWithoutContext> importationSetupWithoutContextList = impSetupWithoutCxtService.findByUserIdAndFilePath(userId, filePath);
            response.setData(importationSetupWithoutContextList);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET findImportationSetupWithoutContextByFilePathAndUserId");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ImportationSetupWithoutContext>> saveImportationSetupWithoutContext(
            HttpServletRequest request,
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext
    ) {
        Response<ImportationSetupWithoutContext> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        importationSetupWithoutContext.setIdUser(idUser);
        try {
            if (importationSetupWithoutContext.getId() == null) {
                importationSetupWithoutContext.setDateCreated(new Date());
                importationSetupWithoutContext.setDateModified(new Date());
                impSetupWithoutCxtService.createOrUpdate(importationSetupWithoutContext);
                response.setData(importationSetupWithoutContext);
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
        log.info("POST saveImportationSetupWithoutContext");
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithoutContext>> updateImportationSetupWithoutContext(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext
    ) {
        Response<ImportationSetupWithoutContext> response = new Response<>();

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
            Optional<ImportationSetupWithoutContext> importSetup = impSetupWithoutCxtService.findById(id);
            if (importSetup.isPresent()) {
                importationSetupWithoutContext.setDateCreated(importSetup.get().getDateCreated());
                importationSetupWithoutContext.setDateModified(new Date());
                impSetupWithoutCxtService.createOrUpdate(importationSetupWithoutContext);
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
        log.info("PUT updateImportationSetupWithoutContext");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> deleteImportationSetupWithoutContext(
            @PathVariable(required = true) String id) {
        Response<String> response = new Response<>();
        try {
            String idDeleted = impSetupWithoutCxtService.delete(id);
            if (idDeleted != null)
                response.setData(idDeleted);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("DELETE deleteImportationSetupWithoutContext");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/load-ngsild-data")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> loadNGSILDDataFromImportSetupWithoutContext(
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @RequestParam boolean samples,
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext
    ) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.loadDataNGSILDByImportationSetupService.loadData(
                    importationSetupWithoutContext, sgeolInstance, userToken
            );
            response.setData(samples ? getSamples(ngsildData) : ngsildData);
            log.info("POST loadNGSILDDataFromImportSetupWithoutContext");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/load-ngsild-data/count")
    public ResponseEntity<Response<Integer>> loadCountNGSILDDataFromImportSetupWithoutContext(
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext
    ) {
        Response<Integer> response = new Response<>();
        try {
            response.setData(
                    this.loadDataNGSILDByImportationSetupService
                            .loadData(importationSetupWithoutContext, sgeolInstance, userToken)
                            .size()
            );
            log.info("POST loadCountNGSILDDataFromImportSetupWithoutContext");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}

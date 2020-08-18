package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.mongodocuments.ImportNGSILDDataSetup;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.ImportNGSILDDataSetupService;
import br.imd.aqueducte.services.LoadDataNGSILDByImportSetupService;
import com.mongodb.DuplicateKeyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static br.imd.aqueducte.utils.RequestsUtils.SGEOL_INSTANCE;
import static br.imd.aqueducte.utils.RequestsUtils.USER_TOKEN;

@RestController
@Log4j2
@RequestMapping("/sync/import-ngsild-data-setup")
@CrossOrigin(origins = "*")
public class ImportNGSILDDataSetupController extends GenericController {

    @Autowired
    private ImportNGSILDDataSetupService service;

    @Autowired
    private LoadDataNGSILDByImportSetupService<ImportNGSILDDataSetup> loadDataNGSILDService;

    @GetMapping(value = "/{idUser}/{importType}/{page}/{count}")
    public ResponseEntity<Response<Page<ImportNGSILDDataSetup>>> findAllImportSetupByUserId(
            @PathVariable("idUser") String idUser,
            @PathVariable("importType") String importType,
            @PathVariable("page") int page,
            @PathVariable("count") int count,
            @RequestParam("useContext") boolean useContext
    ) {
        Response<Page<ImportNGSILDDataSetup>> response = new Response<>();
        try {
            Page<ImportNGSILDDataSetup> importNGSILDDataSetups = service
                    .findByIdUserAndImportTypeAndUseContextOrderByDateCreatedDesc(
                            idUser, importType.toUpperCase(), useContext, page, count
                    );
            if (!importNGSILDDataSetups.hasContent()) {
                response.getErrors().add("Has not content");
                return ResponseEntity.badRequest().body(response);
            }

            response.setData(importNGSILDDataSetups);
            log.info("GET findAllImportSetupByUserId");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.getErrors().add("Error on get Importation Setup with Context list");
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportNGSILDDataSetup>> getImportSetupById(@PathVariable String id) {
        Response<ImportNGSILDDataSetup> response = new Response<>();
        try {
            Optional<ImportNGSILDDataSetup> importNGSILDDataSetup = service.findById(id);
            importNGSILDDataSetup.ifPresent(response::setData);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET getImportSetupById - {}", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Response<List<ImportNGSILDDataSetup>>> findImportSetupByFilePathAndUserId(
            @PathVariable("userId") String userId,
            @RequestParam("filePath") String filePath
    ) {
        Response<List<ImportNGSILDDataSetup>> response = new Response<>();

        if (filePath == null || filePath.equals("")) {
            response.getErrors().add("File path must be informed");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        try {
            List<ImportNGSILDDataSetup> importNGSILDDataSetups = service.findByUserIdAndFilePath(userId, filePath);
            response.setData(importNGSILDDataSetups);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("GET findImportSetupByFilePathAndUserId");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ImportNGSILDDataSetup>> saveImportSetup(
            HttpServletRequest request,
            @RequestBody ImportNGSILDDataSetup importNGSILDDataSetup
    ) {
        Response<ImportNGSILDDataSetup> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        try {
            if (importNGSILDDataSetup.getId() == null) {
                ImportNGSILDDataSetup result = service.createOrUpdate(importNGSILDDataSetup);
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
            return ResponseEntity.badRequest().body(response);
        }
        log.info("POST saveImportSetup");
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Response<ImportNGSILDDataSetup>> updateImportSetup(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody ImportNGSILDDataSetup importNGSILDDataSetup
    ) {
        Response<ImportNGSILDDataSetup> response = new Response<>();

        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        } else if (!importNGSILDDataSetup.getId().equals(id)) {
            response.getErrors().add("Id from payload does not match with id from URL path");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        try {
            Optional<ImportNGSILDDataSetup> importSetup = service.findById(id);
            if (importSetup.isPresent()) {
                ImportNGSILDDataSetup result = service.createOrUpdate(importSetup.get());
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
        log.info("PUT updateImportSetup");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<String>> deleteImportSetupById(@PathVariable String id) {
        Response<String> response = new Response<>();
        try {
            String idDeleted = service.delete(id);
            if (idDeleted != null) {
                response.setData(idDeleted);
            }
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        log.info("DELETE deleteImportSetupById");
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/load-ngsild-data")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> loadNGSILDDataFromImportSetup(
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @RequestBody ImportNGSILDDataSetup importNGSILDDataSetup,
            @RequestParam boolean samples
    ) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.loadDataNGSILDService.loadData(
                    importNGSILDDataSetup, sgeolInstance, userToken
            );
            response.setData(samples ? getSamples(ngsildData) : ngsildData);
            log.info("GET loadNGSILDDataFromImportSetup");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/load-ngsild-data/count")
    public ResponseEntity<Response<Integer>> loadNGSILDDataCountFromImportSetup(
            @RequestHeader(USER_TOKEN) String userToken,
            @RequestHeader(SGEOL_INSTANCE) String sgeolInstance,
            @RequestBody ImportNGSILDDataSetup importNGSILDDataSetup
    ) {
        Response<Integer> response = new Response<>();
        try {
            response.setData(this.loadDataNGSILDService
                    .loadData(importNGSILDDataSetup, sgeolInstance, userToken)
                    .size());
            log.info("GET loadNGSILDDataCountFromImportSetup");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0), e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

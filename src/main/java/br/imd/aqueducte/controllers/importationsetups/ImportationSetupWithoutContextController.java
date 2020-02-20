package br.imd.aqueducte.controllers.importationsetups;

import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.mongodocuments.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.ImportationSetupWithoutContextService;
import br.imd.aqueducte.service.LoadDataNGSILDByImportationSetupService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sync/withoutContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupWithoutContextController extends GenericController {

    @Autowired
    private ImportationSetupWithoutContextService impSetupWithoutCxtService;

    @Autowired
    private LoadDataNGSILDByImportationSetupService<ImportationSetupWithoutContext> loadDataNGSILDByImportationSetupService;

    @GetMapping(value = "/{importType}/{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupWithoutContext>>> findImportTypeImportationSetupWithoutContext(
            @PathVariable("importType") String importType, @PathVariable("page") int page, @PathVariable("count") int count
    ) {
        Response<Page<ImportationSetupWithoutContext>> response = new Response<>();
        try {
            Page<ImportationSetupWithoutContext> list = impSetupWithoutCxtService
                    .findByImportTypeLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(
                            importType.toUpperCase(), page, count
                    );
            if (!list.hasContent()) {
                response.getErrors().add("Has not content");
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(list);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithoutContext>> getByIdImportationSetupWithoutContext(
            @PathVariable(required = true) String id) {

        Response<ImportationSetupWithoutContext> response = new Response<>();
        try {
            Optional<ImportationSetupWithoutContext> importationSetupWithoutContext = impSetupWithoutCxtService
                    .findById(id);
            response.setData(importationSetupWithoutContext.get());
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<ImportationSetupWithoutContext>> saveImportationSetupWithoutContext(
            @ModelAttribute("user-id") String userId,
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext) {
        Response<ImportationSetupWithoutContext> response = new Response<>();

        if (checkUserIdIsEmpty(userId)) {
            response.getErrors().add("Without user id");
            return ResponseEntity.badRequest().body(response);
        }
        importationSetupWithoutContext.setIdUser(userId);
        try {
            if (importationSetupWithoutContext.getId() == null) {
                importationSetupWithoutContext.setDateCreated(new Date());
                importationSetupWithoutContext.setDateModified(new Date());
                impSetupWithoutCxtService.createOrUpdate(importationSetupWithoutContext);
                response.setData(importationSetupWithoutContext);
            } else {
                response.getErrors().add("Object inconsistent");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (DuplicateKeyException e) {
            response.getErrors().add("Duplicate ID");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithoutContext>> updateImportationSetupWithoutContext(
            @ModelAttribute("user-id") String userId,
            @PathVariable String id,
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext
    ) {
        Response<ImportationSetupWithoutContext> response = new Response<>();

        if (checkUserIdIsEmpty(userId)) {
            response.getErrors().add("Without user id");
            return ResponseEntity.badRequest().body(response);
        } else if (!importationSetupWithoutContext.getId().equals(id)) {
            response.getErrors().add("Id from payload does not match with id from URL path");
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
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
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
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // TODO - Pagination ?
    @PostMapping(value = "/load-ngsild-data")
    public ResponseEntity<Response<List<LinkedHashMap<String, Object>>>> loadNGSILDDataFromImportSetupWithoutContext(
            @RequestBody ImportationSetupWithoutContext importationSetupWithoutContext
    ) {
        Response<List<LinkedHashMap<String, Object>>> response = new Response<>();
        try {
            List<LinkedHashMap<String, Object>> ngsildData = this.loadDataNGSILDByImportationSetupService.loadData(importationSetupWithoutContext);
            response.setData(ngsildData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

}

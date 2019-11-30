package br.imd.aqueducte.controllers.importationsetups;

import br.imd.aqueducte.controllers.GenericController;
import br.imd.aqueducte.models.documents.ImportationSetupWithoutContext;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.ImportationSetupWithoutContextService;
import br.imd.aqueducte.treats.withoutcontext.ImportWithoutContextTreat;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/sync/withoutContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupWithoutContextController extends GenericController {

    @Autowired
    private ImportationSetupWithoutContextService impSetupWithoutCxtService;

    @GetMapping(value = "{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupWithoutContext>>> findAllImportationSetupWithoutContext(
            @PathVariable("page") int page, @PathVariable("count") int count) {

        Response<Page<ImportationSetupWithoutContext>> response = new Response<>();
        try {
            Page<ImportationSetupWithoutContext> list = impSetupWithoutCxtService
                    .findAllLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(page, count);
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

    @SuppressWarnings("unchecked")
    @PostMapping
    public ResponseEntity<Response<ImportationSetupWithoutContext>> saveImportationSetupWithoutContext(
            @ModelAttribute("user-id") String userId, @RequestBody Map<String, Object> objectMap) {

        Response<ImportationSetupWithoutContext> response = new Response<>();
        if (userId == null || userId.trim() == "") {
            response.getErrors().add("Without user id");
            return ResponseEntity.badRequest().body(response);
        }
        try {
            List<String> fieldsSelectedForRelationship = (List<String>) objectMap.get("fieldsSelectedForRelationship");
            ImportWithoutContextTreat treat = new ImportWithoutContextTreat();
            ImportationSetupWithoutContext impSetupWithoutCxtRequest = treat.convertToImportationSetupModel(objectMap);

            boolean isExistsImportationSetup = false;
            Optional<ImportationSetupWithoutContext> importationSetupWithoutContextChecked;
            if (impSetupWithoutCxtRequest.getId() != null) {
                importationSetupWithoutContextChecked = impSetupWithoutCxtService
                        .findById(impSetupWithoutCxtRequest.getId());

                if (importationSetupWithoutContextChecked.isPresent()) {
                    impSetupWithoutCxtRequest
                            .setDateCreated(importationSetupWithoutContextChecked.get().getDateCreated());
                    isExistsImportationSetup = true;
                }
            } else {
                impSetupWithoutCxtRequest = impSetupWithoutCxtService.createOrUpdate(impSetupWithoutCxtRequest);
            }
            ImportationSetupWithoutContext impSetupWithoutCxtUpdated = impSetupWithoutCxtService
                    .treatCreateImportationWithoutContextSetup(userId, impSetupWithoutCxtRequest, fieldsSelectedForRelationship,
                            isExistsImportationSetup);
            impSetupWithoutCxtService.createOrUpdate(impSetupWithoutCxtUpdated);

            response.setData(impSetupWithoutCxtUpdated);
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

}

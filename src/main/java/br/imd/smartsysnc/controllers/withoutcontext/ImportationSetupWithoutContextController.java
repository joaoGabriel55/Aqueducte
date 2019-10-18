package br.imd.smartsysnc.controllers.withoutcontext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.DuplicateKeyException;

import br.imd.smartsysnc.models.ImportationSetupWithoutContext;
import br.imd.smartsysnc.models.response.Response;
import br.imd.smartsysnc.processors.withoutcontext.ImportWithoutContextTreat;
import br.imd.smartsysnc.service.ImportationSetupWithoutContextService;

@RestController
@RequestMapping("/sync/withoutContextSetup")
@CrossOrigin(origins = "*")
public class ImportationSetupWithoutContextController {

    @Autowired
    private ImportationSetupWithoutContextService impSetupWithoutCxtService;

    @PostMapping(value = "/convertToNgsild/{layerPath}")
    public ResponseEntity<Response<List<LinkedHashMap<Object, Object>>>> convertToNgsildWithoutContext(
            @PathVariable(required = true) String layerPath, @RequestBody List<Object> data) {
        Response<List<LinkedHashMap<Object, Object>>> response = new Response<>();

        try {
            ImportWithoutContextTreat importWithoutContextTreat = new ImportWithoutContextTreat();
            List<LinkedHashMap<Object, Object>> listNGSILD;
            listNGSILD = importWithoutContextTreat.convertToEntityNGSILD(data, layerPath, null);
            response.setData(listNGSILD);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}")
    public ResponseEntity<Response<Page<ImportationSetupWithoutContext>>> findAllImportationSetupWithoutContext(@PathVariable("page") int page,
                                                                                                                @PathVariable("count") int count) {

        Response<Page<ImportationSetupWithoutContext>> response = new Response<>();
        try {
            Page<ImportationSetupWithoutContext> list = impSetupWithoutCxtService.findAllLabelAndDescriptionAndDateCreatedAndDateModifiedOrderByDateCreated(page, count);
            response.setData(list);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ImportationSetupWithoutContext>> getByIdImportationSetupWithoutContext(@PathVariable(required = true) String id) {

        Response<ImportationSetupWithoutContext> response = new Response<>();
        try {
            Optional<ImportationSetupWithoutContext> importationSetupWithoutContext = impSetupWithoutCxtService.findById(id);
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
            @RequestBody Map<String, Object> objectMap) {

        Response<ImportationSetupWithoutContext> response = new Response<>();

        try {
            List<String> fieldsSelectedForRelationship = (List<String>) objectMap.get("fieldsSelectedForRelationship");
            ImportWithoutContextTreat treat = new ImportWithoutContextTreat();
            ImportationSetupWithoutContext impSetupWithoutCxtRequest = treat.convertToImportationSetupModel(objectMap);

            boolean isExistsImportationSetup = false;
            Optional<ImportationSetupWithoutContext> importationSetupWithoutContextChecked;
            if (impSetupWithoutCxtRequest.getId() != null) {
                importationSetupWithoutContextChecked = impSetupWithoutCxtService.findById(impSetupWithoutCxtRequest.getId());

                if (importationSetupWithoutContextChecked.isPresent()) {
                	impSetupWithoutCxtRequest.setDateCreated(importationSetupWithoutContextChecked.get().getDateCreated());
                    isExistsImportationSetup = true;
                }
            } else {
                impSetupWithoutCxtRequest = impSetupWithoutCxtService.createOrUpdate(
                        impSetupWithoutCxtRequest
                );
            }
            ImportationSetupWithoutContext impSetupWithoutCxtUpdated = impSetupWithoutCxtService.treatCreateImportationWithoutContextSetup(
                    impSetupWithoutCxtRequest,
                    fieldsSelectedForRelationship,
                    isExistsImportationSetup
            );
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
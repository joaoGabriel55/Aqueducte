package br.imd.aqueducte.entitiesrelationship.controllers;

import br.imd.aqueducte.entitiesrelationship.business.EntitiesRelationshipSetupValidate;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipAndTransferService;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.models.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static br.imd.aqueducte.entitiesrelationship.services.implementations.EntitiesRelationshipAndTransferServiceImpl.*;
import static br.imd.aqueducte.models.entitiesrelationship.enums.RelationshipType.*;

@RestController
@RequestMapping("/sync/entitiesRelationshipAndTransfer")
@CrossOrigin(origins = "*")
public class EntitiesRelationshipAndTransferController {

    @Autowired
    private EntitiesRelationshipAndTransferService entitiesRelationshipAndTransferService;

    @Autowired
    private EntitiesRelationshipSetupValidate validator;

    // TODO: Auth
    @PostMapping
    public ResponseEntity<Response<String>> makeEntitiesRelationship(@RequestBody EntitiesRelationshipSetup setup) {
        Response<String> response = new Response<>();
        try {
            int status = STATUS_RELATIONSHIP_NOTHING_TODO;
            List<String> validatorMessage = validator.validateEntitiesRelationshipSetup(setup);
            if (validatorMessage.size() == 0) {
                if (setup.getRelationshipType().equals(MANY_TO_MANY))
                    status = this.entitiesRelationshipAndTransferService.relationshipManyToMany(setup);
                else if (setup.getRelationshipType().equals(ONE_TO_MANY))
                    status = this.entitiesRelationshipAndTransferService.relationshipOneToMany(setup);
                else if (setup.getRelationshipType().equals(ONE_TO_ONE))
                    status = this.entitiesRelationshipAndTransferService.relationshipOneToOne(setup);
            } else {
                response.getErrors().addAll(validatorMessage);
                return ResponseEntity.badRequest().body(response);
            }

            if (status == STATUS_RELATIONSHIP_ERROR) {
                response.getErrors().add("Relationship between entities works wrong.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            if (status == STATUS_RELATIONSHIP_OK)
                response.setData("Relationship between entities done!");
            else if (status == STATUS_RELATIONSHIP_NOTHING_TODO)
                response.setData("Relationship between entities already done.");

        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = {"/transferDataWithRelationshipToOriginLayer/{layer1}/{layer2}"})
    public ResponseEntity<Response<Map<String, String>>> transferDataWithRelationshipToOriginLayer(
            @PathVariable String layer1, @PathVariable String layer2
    ) {
        Response<Map<String, String>> response = new Response<>();

        try {
            CompletableFuture<Integer> data1 = entitiesRelationshipAndTransferService.transferLayerEntitiesAsync(layer1);
            CompletableFuture<Integer> data2 = entitiesRelationshipAndTransferService.transferLayerEntitiesAsync(layer2);
            CompletableFuture.allOf(data1, data2).join();

            Map<String, String> layersTransferResponse = new LinkedHashMap<>();

            int statusTransferData1 = data1.get();
            int statusTransferData2 = data2.get();

            layersTransferResponse.put(layer1, statusMessageTransferData(statusTransferData1));
            layersTransferResponse.put(layer2, statusMessageTransferData(statusTransferData2));

            if (statusTransferData1 == STATUS_TRANSFER_NOTHING_TODO &&
                    statusTransferData2 == STATUS_TRANSFER_NOTHING_TODO) {
                response.setData(layersTransferResponse);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (statusTransferData1 == STATUS_TRANSFER_ERROR &&
                    statusTransferData2 == STATUS_TRANSFER_ERROR) {
                response.setData(layersTransferResponse);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.setData(layersTransferResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (final Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String statusMessageTransferData(int status) {
        if (status == STATUS_TRANSFER_OK)
            return "Transfer processed Entities done!";
        else if (status == STATUS_TRANSFER_NOTHING_TODO)
            return "Nothing to do!";
        else if (status == STATUS_TRANSFER_ERROR)
            return "Transfer processed Entities does not work!";
        return null;
    }


}

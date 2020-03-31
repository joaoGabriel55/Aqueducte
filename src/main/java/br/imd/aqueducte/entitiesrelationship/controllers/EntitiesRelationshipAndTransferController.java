package br.imd.aqueducte.entitiesrelationship.controllers;

import br.imd.aqueducte.entitiesrelationship.business.EntitiesRelationshipSetupValidate;
import br.imd.aqueducte.entitiesrelationship.services.EntitiesRelationshipAndTransferService;
import br.imd.aqueducte.models.entitiesrelationship.enums.EntitiesRelationshipSetupStatus;
import br.imd.aqueducte.models.entitiesrelationship.mongodocuments.EntitiesRelationshipSetup;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.TaskStatusService;
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
@RequestMapping("/sync/entitiesRelationship")
@CrossOrigin(origins = "*")
public class EntitiesRelationshipAndTransferController {

    @Autowired
    private EntitiesRelationshipAndTransferService entitiesRelationshipAndTransferService;

    @Autowired
    private EntitiesRelationshipSetupValidate validator;

    @Autowired
    private TaskStatusService taskStatusService;

    @PostMapping(value = {"/make", "/make/{taskId}"})
    public ResponseEntity<Response<Map<String, String>>> makeEntitiesRelationship(
            @PathVariable String taskId,
            @RequestBody EntitiesRelationshipSetup setup
    ) {
        Response<Map<String, String>> response = new Response<>();
        Map<String, String> statusRelationship = new LinkedHashMap<>();
        try {
            int status = STATUS_RELATIONSHIP_NOTHING_TODO;
            List<String> validatorMessage = validator.validateEntitiesRelationshipSetup(setup);
            if (validatorMessage.size() == 0 && setup.getStatus().equals(EntitiesRelationshipSetupStatus.DONE)) {
                if (setup.getRelationshipType().equals(MANY_TO_MANY))
                    status = this.entitiesRelationshipAndTransferService.relationshipManyToMany(setup);
                else if (setup.getRelationshipType().equals(ONE_TO_MANY))
                    status = this.entitiesRelationshipAndTransferService.relationshipOneToMany(setup);
                else if (setup.getRelationshipType().equals(ONE_TO_ONE))
                    status = this.entitiesRelationshipAndTransferService.relationshipOneToOne(setup);
            } else {
                response.getErrors().addAll(validatorMessage);
                taskStatusService.sendTaskStatusProgress(
                        taskId,
                        TaskStatus.ERROR,
                        "Erro: Relacionamento entre as entidades das Layers " +
                                setup.getLayerSetup().get(0).getName() + " e " +
                                setup.getLayerSetup().get(0).getName(),
                        "status-relationship-process"
                );
                return ResponseEntity.badRequest().body(response);
            }

            if (status == STATUS_RELATIONSHIP_ERROR) {
                response.getErrors().add("Relationship between entities works wrong.");
                taskStatusService.sendTaskStatusProgress(
                        taskId,
                        TaskStatus.ERROR,
                        "Erro: Relacionamento entre as entidades das Layers " +
                                setup.getLayerSetup().get(0).getName() + " e " +
                                setup.getLayerSetup().get(0).getName(),
                        "status-relationship-process"
                );
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            if (status == STATUS_RELATIONSHIP_OK) {
                statusRelationship.put("status", "STATUS_RELATIONSHIP_OK");
                statusRelationship.put("message", "Relationship between entities done!");
                response.setData(statusRelationship);
            } else if (status == STATUS_RELATIONSHIP_NOTHING_TODO) {
                statusRelationship.put("status", "STATUS_RELATIONSHIP_NOTHING_TODO");
                statusRelationship.put("message", "Relationship between entities already done.");
                response.setData(statusRelationship);
            }

        } catch (Exception e) {
            taskStatusService.sendTaskStatusProgress(
                    taskId,
                    TaskStatus.ERROR,
                    "Erro: Relacionamento entre as entidades das Layers " +
                            setup.getLayerSetup().get(0).getName() + " e " +
                            setup.getLayerSetup().get(0).getName(),
                    "status-relationship-process"
            );
            response.getErrors().add(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        taskStatusService.sendTaskStatusProgress(
                taskId,
                TaskStatus.DONE,
                "Sucesso: Relacionamento entre as entidades das Layers " +
                        setup.getLayerSetup().get(0).getName() + " e " +
                        setup.getLayerSetup().get(0).getName(),
                "status-relationship-process"
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = {"/transfer/{layer1}/{layer2}", "/transfer/{layer1}/{layer2}/{taskId}"})
    public ResponseEntity<Response<Map<String, String>>> transferDataWithRelationshipToOriginLayer(
            @PathVariable String layer1, @PathVariable String layer2, @PathVariable String taskId
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
                taskStatusService.sendTaskStatusProgress(
                        taskId,
                        TaskStatus.ERROR,
                        "Transferência de entidades das Layers " +
                                layer1 + "(" + layersTransferResponse.get(layer1) + ") " +
                                layer2 + "(" + layersTransferResponse.get(layer2) + ")",
                        "status-transfer-process"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (statusTransferData1 == STATUS_TRANSFER_ERROR &&
                    statusTransferData2 == STATUS_TRANSFER_ERROR) {
                response.setData(layersTransferResponse);
                taskStatusService.sendTaskStatusProgress(
                        taskId,
                        TaskStatus.ERROR,
                        "Transferência de entidades das Layers " +
                                layer1 + "(" + layersTransferResponse.get(layer1) + ") " +
                                layer2 + "(" + layersTransferResponse.get(layer2) + ")",
                        "status-transfer-process"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response.setData(layersTransferResponse);
            taskStatusService.sendTaskStatusProgress(
                    taskId,
                    TaskStatus.DONE,
                    "Transferência de entidades das Layers " +
                            layer1 + "(" + layersTransferResponse.get(layer1) + ") " +
                            layer2 + "(" + layersTransferResponse.get(layer2) + ")",
                    "status-transfer-process"
            );
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

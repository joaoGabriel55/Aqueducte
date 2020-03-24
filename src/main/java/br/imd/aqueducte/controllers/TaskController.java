package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.TaskStatusService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping("/sync/task")
@CrossOrigin(origins = "*")
public class TaskController extends GenericController {

    @Autowired
    private TaskStatusService taskStatusService;


    @PostMapping(value = "/topic/{topicName}/{taskId}")
    public ResponseEntity<Response<Task>> receiveSendAndSaveTaskToWebSocketTopic(@PathVariable String topicName,
                                                                                 @PathVariable String taskId,
                                                                                 @RequestBody Task task
    ) {
        Response<Task> response = new Response<>();
        if (task != null) {
            try {
                Task taskSent = taskStatusService.sendTaskStatusProgress(taskId, task.getStatus(), task.getDescription(), topicName);
                if (taskSent == null) {
                    response.getErrors().add("Error on send message to web socket topic");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
                response.setData(taskSent);
            } catch (Exception e) {
                e.printStackTrace();
                response.getErrors().add(e.getLocalizedMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.getErrors().add("Error on send message to web socket topic");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<Task>> saveImportationSetupWithContext(HttpServletRequest request,
                                                                          @RequestBody Task task
    ) {
        Response<Task> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            return ResponseEntity.badRequest().body(response);
        }
        task.setUserId(idUser);
        try {
            if (task.getId() != null) {
                if (taskStatusService.findById(task.getId()).isPresent()) {
                    response.getErrors().add("Task already exists");
                    return ResponseEntity.badRequest().body(response);
                }
                response.getErrors().add("Object inconsistent");
                return ResponseEntity.badRequest().body(response);
            } else {
                task.setDateCreated(new Date());
                task.setDateModified(new Date());
                Task taskCreated = taskStatusService.createOrUpdate(task);
                response.setData(taskCreated);
            }
        } catch (DuplicateKeyException e) {
            response.getErrors().add("Duplicate ID");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{userId}/{page}/{count}")
    public ResponseEntity<Response<Page<Task>>> findByUserIdWithFilters(@PathVariable String userId,
                                                                        @PathVariable Integer page,
                                                                        @PathVariable Integer count,
                                                                        @RequestParam String type,
                                                                        @RequestParam String status
    ) {
        Response<Page<Task>> response = new Response<>();
        if ((type == "" || type == null) && (status == "" || status == null)) {
            Page<Task> tasks = taskStatusService.findByUserId(userId, page, count);
            response.setData(tasks);
        } else if ((type != "" || type != null) && (status == "" || status == null)) {
            Page<Task> tasks = taskStatusService.findByUserIdAndType(userId, type, page, count);
            if (tasks == null) {
                response.getErrors().add("The \"type\" informed not exists");
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(tasks);
        } else if ((type == "" || type == null) && (status != "" || status != null)) {
            Page<Task> tasks = taskStatusService.findByUserIdAndStatus(userId, status, page, count);
            if (tasks == null) {
                response.getErrors().add("The \"status\" informed not exists");
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(tasks);
        } else if ((type != "" || type != null) && (status != "" || status != null)) {
            Page<Task> tasks = taskStatusService.findByUserIdAndTypeAndStatus(userId, type, status, page, count);
            if (tasks == null && !tasks.hasContent()) {
                response.getErrors().add("The \"type\" \"status\" informed not exists");
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(tasks);
        }
        return ResponseEntity.ok().body(response);
    }

}

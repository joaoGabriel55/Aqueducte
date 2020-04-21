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

import static br.imd.aqueducte.logger.LoggerMessage.logError;
import static br.imd.aqueducte.logger.LoggerMessage.logInfo;

@SuppressWarnings("ALL")
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
                    logError(response.getErrors().get(0), null);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
                response.setData(taskSent);
                logInfo("POST receiveSendAndSaveTaskToWebSocketTopic", null);
            } catch (Exception e) {
                e.printStackTrace();
                response.getErrors().add(e.getLocalizedMessage());
                logError(response.getErrors().get(0), e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.getErrors().add("Error on send message to web socket topic");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<Task>> saveTask(HttpServletRequest request, @RequestBody Task task) {
        Response<Task> response = new Response<>();
        if (checkUserIdIsEmpty(request)) {
            response.getErrors().add("Without user id");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        }
        task.setUserId(idUser);
        try {
            if (task.getId() != null) {
                if (taskStatusService.findById(task.getId()).isPresent()) {
                    response.getErrors().add("Task already exists");
                    logError(response.getErrors().get(0), null);
                    return ResponseEntity.badRequest().body(response);
                }
                response.getErrors().add("Object inconsistent");
                logError(response.getErrors().get(0), null);
                return ResponseEntity.badRequest().body(response);
            } else {
                task.setDateCreated(new Date());
                task.setDateModified(new Date());
                Task taskCreated = taskStatusService.createOrUpdate(task);
                response.setData(taskCreated);
                logInfo("POST saveTask", null);
            }
        } catch (DuplicateKeyException e) {
            response.getErrors().add("Duplicate ID");
            logError(response.getErrors().get(0), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            logError(response.getErrors().get(0), null);
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
        if ((type == null || type.equals("")) && (status == null || status.equals(""))) {
            Page<Task> tasks = taskStatusService.findByUserId(userId, page, count);
            response.setData(tasks);
        } else if ((type != null && !type.equals("")) && (status == null || status.equals(""))) {
            Page<Task> tasks = taskStatusService.findByUserIdAndType(userId, type, page, count);
            if (tasks == null) {
                response.getErrors().add("The \"type\" informed not exists");
                logError(response.getErrors().get(0), null);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(tasks);
        } else if ((type == null || type.equals("")) && (status != null || !status.equals(""))) {
            Page<Task> tasks = taskStatusService.findByUserIdAndStatus(userId, status, page, count);
            if (tasks == null) {
                response.getErrors().add("The \"status\" informed not exists");
                logError(response.getErrors().get(0), null);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(tasks);
        } else if ((type != null || !type.equals("")) && (status != null || !status.equals(""))) {
            Page<Task> tasks = taskStatusService.findByUserIdAndTypeAndStatus(userId, type, status, page, count);
            if (tasks == null && !tasks.hasContent()) {
                response.getErrors().add("The \"type\" \"status\" informed not exists");
                logError(response.getErrors().get(0), null);
                return ResponseEntity.badRequest().body(response);
            }
            response.setData(tasks);
        }
        logInfo("GET findByUserIdWithFilters", null);
        return ResponseEntity.ok().body(response);
    }

}

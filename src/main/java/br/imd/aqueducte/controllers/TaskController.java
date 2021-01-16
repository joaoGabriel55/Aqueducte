package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.services.TaskStatusService;
import com.mongodb.DuplicateKeyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("ALL")
@RestController
@Log4j2
@RequestMapping("/sync/task")
@CrossOrigin(origins = "*")
public class TaskController extends GenericController {

    @Autowired
    private TaskStatusService taskStatusService;


    @PostMapping(value = "/topic/{topicName}/{taskId}")
    public ResponseEntity<Response<Task>> receiveSendAndSaveTaskToWebSocketTopic(
            @PathVariable String topicName,
            @PathVariable String taskId,
            @RequestBody Map<String, Object> payload
    ) {
        Response<Task> response = new Response<>();
        if (payload != null) {
            try {
                if (payload.containsKey("time"))
                    showRoundTripTime((Double) payload.get("time"));
                Task task = toTask(payload);
                Task taskSent = taskStatusService.sendTaskStatusProgress(
                        taskId, task.getStatus(), task.getDescription(), topicName
                );
                if (taskSent == null) {
                    response.getErrors().add("Error on send message to web socket topic");
                    log.error(response.getErrors().get(0));
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
                response.setData(taskSent);
                log.info("POST receiveSendAndSaveTaskToWebSocketTopic");
            } catch (Exception e) {
                e.printStackTrace();
                response.getErrors().add(e.getLocalizedMessage());
                log.error(response.getErrors().get(0), e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            response.getErrors().add("Error on send message to web socket topic");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<Response<Task>> saveTask(@RequestBody Task task) {
        Response<Task> response = new Response<>();
        try {
            if (task.getId() != null) {
                if (taskStatusService.findById(task.getId()).isPresent()) {
                    response.getErrors().add("Task already exists");
                    log.error(response.getErrors().get(0));
                    return ResponseEntity.badRequest().body(response);
                }
                response.getErrors().add("Object inconsistent");
                log.error(response.getErrors().get(0));
                return ResponseEntity.badRequest().body(response);
            } else {
                task.setDateCreated(new Date());
                task.setDateModified(new Date());
                Task taskCreated = taskStatusService.createOrUpdate(task);
                response.setData(taskCreated);
            }
        } catch (DuplicateKeyException e) {
            response.getErrors().add("Duplicate ID");
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(response.getErrors().get(0));
            return ResponseEntity.badRequest().body(response);
        }
        log.info("POST saveTask successfuly");
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
        try {
            if ((type == null || type.equals("")) && (status == null || status.equals(""))) {
                Page<Task> tasks = taskStatusService.findByUserId(userId, page, count);
                response.setData(tasks);
            } else if ((type != null && !type.equals("")) && (status == null || status.equals(""))) {
                Page<Task> tasks = taskStatusService.findByUserIdAndType(userId, type, page, count);
                if (tasks == null) {
                    response.getErrors().add("The \"type\" informed not exists");
                    log.error(response.getErrors().get(0));
                    return ResponseEntity.badRequest().body(response);
                }
                response.setData(tasks);
            } else if ((type == null || type.equals("")) && (status != null || !status.equals(""))) {
                Page<Task> tasks = taskStatusService.findByUserIdAndStatus(userId, status, page, count);
                if (tasks == null) {
                    response.getErrors().add("The \"status\" informed not exists");
                    log.error(response.getErrors().get(0));
                    return ResponseEntity.badRequest().body(response);
                }
                response.setData(tasks);
            } else if ((type != null || !type.equals("")) && (status != null || !status.equals(""))) {
                Page<Task> tasks = taskStatusService.findByUserIdAndTypeAndStatus(userId, type, status, page, count);
                if (tasks == null && !tasks.hasContent()) {
                    response.getErrors().add("The \"type\" \"status\" informed not exists");
                    log.error(response.getErrors().get(0));
                    return ResponseEntity.badRequest().body(response);
                }
                response.setData(tasks);
            }
            log.info("GET findByUserIdWithFilters successfuly");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.getErrors().add(e.getMessage());
            log.error(e.getMessage(), e.getStackTrace());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private void showRoundTripTime(Double sendTime) {
        Long receivedTime = Calendar.getInstance().getTimeInMillis();
        log.info("Received time -> {}", receivedTime);
        log.info("HTTP - Round Trip Time (RTT) -> {}", Double.parseDouble(receivedTime.toString()) - sendTime);
    }

    private static Task toTask(Map<String, Object> data) throws IOException, ClassNotFoundException {
        log.info("Receiving task message: {}", data.toString());

        if (!data.containsKey("status") || !data.containsKey("description")) {
            String msg = "Task must contain: \"status\" and \"description\"";
            log.error(msg);
            throw new NullPointerException(msg);
        }

        Task task = new Task();
        task.setDescription(data.get("description").toString());
        task.setStatus(TaskStatus.valueOf(data.get("status").toString()));
        return task;
    }
}

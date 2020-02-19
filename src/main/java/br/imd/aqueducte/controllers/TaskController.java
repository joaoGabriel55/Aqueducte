package br.imd.aqueducte.controllers;

import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.models.response.Response;
import br.imd.aqueducte.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sync/task")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskStatusService taskStatusService;


    @PostMapping(value = "/topic/{topicName}")
    public ResponseEntity<Response<Task>> sendAndSaveTaskToWebSocketTopic(@PathVariable String topicName,
                                                                                      @RequestBody Task task
    ) {
        Response<Task> response = new Response<>();
        response.setData(task);
        taskStatusService.sendTaskStatusProgress(task, topicName);
        // TODO Save Task
        return ResponseEntity.ok().body(response);
    }


}

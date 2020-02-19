package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.service.TaskStatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusServiceImpl implements TaskStatusService {
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_ERROR = "error";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Override
    public Task sendTaskStatusProgress(Task task, String topicName) {
        if (task.getId() != null && task.getIndex() != null) {
            try {
                String destination = "/topic/" + topicName;
                this.messageTemplate.convertAndSend(destination, mapper.writeValueAsString(task));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return task;
    }
}

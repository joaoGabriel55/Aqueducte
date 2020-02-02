package br.imd.aqueducte.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;

public class TaskStatusServiceImpl {
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_ERROR = "error";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    public Map<String, Object> sendTaskStatusProgress(Map<String, Object> response,
                                                      String taskId,
                                                      Integer taskIndex,
                                                      String status
    ) {
        if (taskId != null && taskIndex != null) {
            try {
                response.put("id", taskId);
                response.put("index", taskIndex);
                response.put("status", status);
                this.messageTemplate.convertAndSend(
                        "/topic/status-task-import-process",
                        mapper.writeValueAsString(response)
                );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}

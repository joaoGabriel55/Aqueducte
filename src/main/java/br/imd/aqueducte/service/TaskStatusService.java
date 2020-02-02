package br.imd.aqueducte.service;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface TaskStatusService {
    Map<String, Object> sendStatusProgress(Map<String, Object> response,
                                           String taskId,
                                           Integer taskIndex,
                                           String status);
}

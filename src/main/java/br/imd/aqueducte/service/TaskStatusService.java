package br.imd.aqueducte.service;

import br.imd.aqueducte.models.mongodocuments.Task;
import org.springframework.stereotype.Component;

@Component
public interface TaskStatusService {
    Task sendTaskStatusProgress(Task task, String topicName);
}

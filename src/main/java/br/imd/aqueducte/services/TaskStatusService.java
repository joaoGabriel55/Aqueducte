package br.imd.aqueducte.services;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.mongodocuments.Task;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface TaskStatusService extends GenericService<Task> {

    Task sendTaskStatusProgress(String taskId, TaskStatus status, String description, String topicName) throws Exception;

    Page<Task> findByUserId(String userId, int page, int count) throws Exception;

    Page<Task> findByType(String type, int page, int count) throws Exception;

    Page<Task> findByUserIdAndType(String userId, String type, int page, int count) throws Exception;

    Page<Task> findByUserIdAndStatus(String userId, String status, int page, int count) throws Exception;

    Page<Task> findByUserIdAndTypeAndStatus(String userId, String type, String status, int page, int count) throws Exception;

}

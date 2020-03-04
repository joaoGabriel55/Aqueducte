package br.imd.aqueducte.service.implementation;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.repositories.TaskRepository;
import br.imd.aqueducte.service.TaskStatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ALL")
@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Override
    public Task sendTaskStatusProgress(String taskId, TaskStatus status, String description, String topicName) {
        if (taskId != null) {
            Optional<Task> taskFound = findById(taskId);
            if (taskFound.isPresent()) {
                try {
                    taskFound.get().setStatus(status);
                    taskFound.get().setDescription(description);
                    taskFound.get().setDateModified(new Date());
                    Task taskStatusUpdated = createOrUpdate(taskFound.get());
                    if (taskStatusUpdated == null) {
                        return null;
                    }
                    String destination = "/topic/" + topicName;
                    this.messageTemplate.convertAndSend(destination, mapper.writeValueAsString(taskStatusUpdated));
                    return taskStatusUpdated;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public Page<Task> findByType(String type, int page, int count) {
        PageRequest pageable = new PageRequest(page, count);
        return this.taskRepository.findByType(type, pageable);
    }

    @Override
    public Page<Task> findByUserId(String userId, int page, int count) {
        PageRequest pageable = new PageRequest(page, count);
        return this.taskRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Task> findByUserIdAndType(String userId, String type, int page, int count) {
        if (checkTaskType(type) == null)
            return null;
        PageRequest pageable = new PageRequest(page, count);
        return this.taskRepository.findByUserIdAndType(userId, type.toUpperCase(), pageable);
    }

    @Override
    public Page<Task> findByUserIdAndStatus(String userId, String status, int page, int count) {
        if (checkTaskStatus(status) == null)
            return null;
        PageRequest pageable = new PageRequest(page, count);
        return this.taskRepository.findByUserIdAndStatus(userId, status.toUpperCase(), pageable);
    }

    @Override
    public Page<Task> findByUserIdAndTypeAndStatus(String userId, String type, String status, int page, int count) {
        if (checkTaskType(type) == null && checkTaskStatus(status) == null)
            return null;
        PageRequest pageable = new PageRequest(page, count);
        return this.taskRepository.findByUserIdAndTypeAndStatus(userId, type.toUpperCase(), status.toUpperCase(), pageable);
    }

    @Override
    public Task createOrUpdate(Task obj) {
        return this.taskRepository.save(obj);
    }

    @Override
    public List<Task> findAll() {
        return null;
    }

    @Override
    public Optional<Task> findById(String id) {
        return this.taskRepository.findById(id);
    }

    @Override
    public String delete(String id) {
        Optional<Task> taskFound = this.taskRepository.findById(id);
        if (!taskFound.isPresent())
            return null;
        String idForDelete = taskFound.get().getId();
        this.taskRepository.deleteById(idForDelete);
        return idForDelete;
    }

    private TaskType checkTaskType(String type) {
        if (type.equalsIgnoreCase(TaskType.IMPORT_DATA.name())) {
            return TaskType.IMPORT_DATA;
        } else if (type.equalsIgnoreCase(TaskType.UPLOAD_FILE.name())) {
            return TaskType.UPLOAD_FILE;
        } else if (type.equalsIgnoreCase(TaskType.RELATIONSHIP_ENTITIES.name())) {
            return TaskType.RELATIONSHIP_ENTITIES;
        }
        return null;
    }

    private TaskStatus checkTaskStatus(String status) {
        if (status.equalsIgnoreCase(TaskStatus.PROCESSING.name())) {
            return TaskStatus.PROCESSING;
        } else if (status.equalsIgnoreCase(TaskStatus.ERROR.name())) {
            return TaskStatus.ERROR;
        } else if (status.equalsIgnoreCase(TaskStatus.DONE.name())) {
            return TaskStatus.DONE;
        }
        return null;
    }
}

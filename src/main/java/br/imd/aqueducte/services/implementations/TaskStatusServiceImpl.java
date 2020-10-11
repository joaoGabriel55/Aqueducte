package br.imd.aqueducte.services.implementations;

import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import br.imd.aqueducte.models.mongodocuments.Task;
import br.imd.aqueducte.repositories.TaskRepository;
import br.imd.aqueducte.services.TaskStatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ALL")
@Service
@Log4j2
public class TaskStatusServiceImpl implements TaskStatusService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Override
    public Task sendTaskStatusProgress(String taskId, TaskStatus status, String description, String topicName) throws Exception {
        if (taskId == null) {
            log.error("Task id is null", taskId);
            throw new Exception();
        }
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
                log.info("sendTaskStatusProgress - taskId: {} status: {} description: {} topicName: {}",
                        taskId, status, description, topicName
                );
                return taskStatusUpdated;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e.getStackTrace());
                throw new Exception();
            }
        } else {
            log.error("Task not found - {}", taskId);
            throw new Exception();
        }
    }

    @Override
    public Page<Task> findByType(String type, int page, int count) throws Exception {
        try {
            checkTaskType(type);
            PageRequest pageable = PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "dateCreated"));
            log.info("findByType Task - type: {} page: {} count: {}", type, page, count);
            return this.taskRepository.findByType(type, pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<Task> findByUserId(String userId, int page, int count) throws Exception {
        try {
            log.info("findByType Task - userId: {} page: {} count: {}", userId, page, count);
            PageRequest pageable = PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "dateCreated"));
            Page<Task> tasksPageable = this.taskRepository.findByUserId(userId, pageable);
            return tasksPageable;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<Task> findByUserIdAndType(String userId, String type, int page, int count) throws Exception {
        try {
            checkTaskType(type);
            PageRequest pageable = PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "dateCreated"));
            log.info("findByType Task - userId: {} type: {} page: {} count: {}", userId, type, page, count);
            return this.taskRepository.findByUserIdAndType(userId, type.toUpperCase(), pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<Task> findByUserIdAndStatus(String userId, String status, int page, int count) throws Exception {
        try {
            checkTaskStatus(status);
            PageRequest pageable = PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "dateCreated"));
            log.info("findByType Task - userId: {} status: {} page: {} count: {}", userId, status, page, count);
            return this.taskRepository.findByUserIdAndStatus(userId, status.toUpperCase(), pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Page<Task> findByUserIdAndTypeAndStatus(String userId, String type, String status, int page, int count) throws Exception {
        try {
            checkTaskType(type);
            checkTaskStatus(status);
            PageRequest pageable = PageRequest.of(page, count, Sort.by(Sort.Direction.DESC, "dateCreated"));
            log.info("findByType Task - userId: {} type: {} status: {} page: {} count: {}", userId, type, status, page, count);
            return this.taskRepository.findByUserIdAndTypeAndStatus(userId, type.toUpperCase(), status.toUpperCase(), pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public Task createOrUpdate(Task obj) throws Exception {
        try {
            log.info("createOrUpdate Task");
            return this.taskRepository.save(obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public List<Task> findAll() {
        return null;
    }

    @Override
    public Optional<Task> findById(String id) throws Exception {
        try {
            log.info("findById Task - {}", id);
            return this.taskRepository.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    @Override
    public String delete(String id) throws Exception {
        try {
            Optional<Task> taskFound = this.taskRepository.findById(id);
            if (!taskFound.isPresent()) {
                log.error("Task not found - {}", id);
                throw new Exception();
            }

            String idForDelete = taskFound.get().getId();
            this.taskRepository.deleteById(idForDelete);
            log.info("findById Task - {}", id);
            return idForDelete;
        } catch (Exception e) {
            log.error(e.getMessage(), e.getStackTrace());
            throw new Exception();
        }
    }

    private TaskType checkTaskType(String type) throws Exception {
        if (type.equalsIgnoreCase(TaskType.IMPORT_DATA.name())) {
            return TaskType.IMPORT_DATA;
        } else if (type.equalsIgnoreCase(TaskType.UPLOAD_FILE.name())) {
            return TaskType.UPLOAD_FILE;
        } else if (type.equalsIgnoreCase(TaskType.RELATIONSHIP_ENTITIES.name())) {
            return TaskType.RELATIONSHIP_ENTITIES;
        }
        log.error("type is invalid - {}", type);
        throw new Exception();
    }

    private TaskStatus checkTaskStatus(String status) throws Exception {
        if (status.equalsIgnoreCase(TaskStatus.PROCESSING.name())) {
            return TaskStatus.PROCESSING;
        } else if (status.equalsIgnoreCase(TaskStatus.ERROR.name())) {
            return TaskStatus.ERROR;
        } else if (status.equalsIgnoreCase(TaskStatus.DONE.name())) {
            return TaskStatus.DONE;
        }
        log.error("status is invalid - {}", status);
        throw new Exception();
    }
}

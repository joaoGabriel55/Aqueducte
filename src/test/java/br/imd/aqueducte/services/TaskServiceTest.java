package br.imd.aqueducte.services;


import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import br.imd.aqueducte.models.mongodocuments.Task;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@Log4j2
public class TaskServiceTest extends AqueducteApplicationTests {

    @Autowired
    private TaskStatusService taskStatusService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void saveTest() throws Exception {
        Task task = new Task("abc", "Task1", TaskType.IMPORT_DATA, TaskStatus.ERROR, "Sad Error", new Date(), new Date());
        Task createdTask = taskStatusService.createOrUpdate(task);
        assertEquals(task, createdTask);
    }

    @Test
    public void updateTest() throws Exception {
        Task task = new Task("abc", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task createdTask = taskStatusService.createOrUpdate(task);

        createdTask.setStatus(TaskStatus.DONE);
        createdTask.setDateModified(new Date());
        Task updatedTask = taskStatusService.createOrUpdate(createdTask);

        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }


    @Test
    public void findByTypeTest() throws Exception {

        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());

        taskStatusService.createOrUpdate(task1);
        taskStatusService.createOrUpdate(task2);
        taskStatusService.createOrUpdate(task3);

        Page<Task> tasksImportData = taskStatusService.findByType(TaskType.IMPORT_DATA.name(), 0, 5);
        Page<Task> tasksUploadFile = taskStatusService.findByType(TaskType.UPLOAD_FILE.name(), 0, 5);
        assertEquals(2, tasksImportData.getTotalElements());
        assertEquals(1, tasksUploadFile.getTotalElements());
    }

    @Test
    public void findByUserIdAndTypeTest() throws Exception {

        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.RELATIONSHIP_ENTITIES, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task4 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task5 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.ERROR, "Sad Error", new Date(), new Date());

        taskStatusService.createOrUpdate(task1);
        taskStatusService.createOrUpdate(task2);
        taskStatusService.createOrUpdate(task3);
        taskStatusService.createOrUpdate(task4);
        taskStatusService.createOrUpdate(task5);

        Page<Task> tasksImportDataUser1 = taskStatusService.findByUserIdAndType(
                task1.getUserId(), TaskType.IMPORT_DATA.name(), 0, 5
        );
        Page<Task> tasksRelationshipUser1 = taskStatusService.findByUserIdAndType(
                task1.getUserId(), TaskType.IMPORT_DATA.name(), 0, 5
        );
        Page<Task> tasksImportDataUser2 = taskStatusService.findByUserIdAndType(
                task3.getUserId(), TaskType.IMPORT_DATA.name(), 0, 5
        );
        Page<Task> tasksUploadFileUser2 = taskStatusService.findByUserIdAndType(
                task3.getUserId(), TaskType.UPLOAD_FILE.name(), 0, 5
        );

        assertEquals(1, tasksImportDataUser1.getTotalElements());
        assertEquals(1, tasksRelationshipUser1.getTotalElements());
        assertEquals(1, tasksImportDataUser2.getTotalElements());
        assertEquals(2, tasksUploadFileUser2.getTotalElements());
    }

    @Test
    public void findByUserIdAndStatusTest() throws Exception {

        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.RELATIONSHIP_ENTITIES, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task4 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task5 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.ERROR, "Sad Error", new Date(), new Date());

        taskStatusService.createOrUpdate(task1);
        taskStatusService.createOrUpdate(task2);
        taskStatusService.createOrUpdate(task3);
        taskStatusService.createOrUpdate(task4);
        taskStatusService.createOrUpdate(task5);

        Page<Task> tasksImportDataUser1 = taskStatusService.findByUserIdAndStatus(
                task1.getUserId(), TaskStatus.PROCESSING.name(), 0, 5
        );
        Page<Task> tasksRelationshipUser1 = taskStatusService.findByUserIdAndStatus(
                task1.getUserId(), TaskStatus.DONE.name(), 0, 5
        );
        Page<Task> tasksImportDataUser2 = taskStatusService.findByUserIdAndStatus(
                task3.getUserId(), TaskStatus.DONE.name(), 0, 5
        );
        Page<Task> tasksUploadFileUser2 = taskStatusService.findByUserIdAndStatus(
                task3.getUserId(), TaskStatus.ERROR.name(), 0, 5
        );

        assertEquals(1, tasksImportDataUser1.getTotalElements());
        assertEquals(1, tasksRelationshipUser1.getTotalElements());
        assertEquals(2, tasksImportDataUser2.getTotalElements());
        assertEquals(1, tasksUploadFileUser2.getTotalElements());

    }

    @Test
    public void findByUserIdAndTypeAndStatusTest() throws Exception {
        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.RELATIONSHIP_ENTITIES, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task4 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task5 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.ERROR, "Sad Error", new Date(), new Date());

        taskStatusService.createOrUpdate(task1);
        taskStatusService.createOrUpdate(task2);
        taskStatusService.createOrUpdate(task3);
        taskStatusService.createOrUpdate(task4);
        taskStatusService.createOrUpdate(task5);

        Page<Task> tasksImportDataUser1 = taskStatusService.findByUserIdAndTypeAndStatus(
                task1.getUserId(), TaskType.IMPORT_DATA.name(), TaskStatus.PROCESSING.name(), 0, 5
        );
        Page<Task> tasksRelationshipUser2 = taskStatusService.findByUserIdAndTypeAndStatus(
                task3.getUserId(), TaskType.IMPORT_DATA.name(), TaskStatus.DONE.name(), 0, 5
        );

        assertEquals(1, tasksImportDataUser1.getTotalElements());
        assertEquals(1, tasksRelationshipUser2.getTotalElements());

    }

    @Override
    protected void close() throws Exception {
        super.close();
    }

    @After
    public void tearDown() {
        final String collection = "task";
        log.info("Dropping collection: {} ", collection);
        mongoTemplate.dropCollection(collection);
    }
}

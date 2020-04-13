package br.imd.aqueducte.services;


import br.imd.aqueducte.AqueducteApplicationTests;
import br.imd.aqueducte.models.enums.TaskStatus;
import br.imd.aqueducte.models.enums.TaskType;
import br.imd.aqueducte.models.mongodocuments.Task;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TaskServiceTest extends AqueducteApplicationTests {

    @Autowired
    private TaskStatusService taskStatusService;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
    }

    @Test
    public void saveTest() {
        Task task = new Task("abc", "Task1", TaskType.IMPORT_DATA, TaskStatus.ERROR, "Sad Error", new Date(), new Date());
        Task createdTask = taskStatusService.createOrUpdate(task);
        assertEquals(task, createdTask);
        taskStatusService.delete(createdTask.getId());
    }

    @Test
    public void updateTest() {
        Task task = new Task("abc", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task createdTask = taskStatusService.createOrUpdate(task);

        createdTask.setStatus(TaskStatus.DONE);
        createdTask.setDateModified(new Date());
        Task updatedTask = taskStatusService.createOrUpdate(createdTask);

        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
        taskStatusService.delete(createdTask.getId());
        mongoTemplate.dropCollection("task_test");
    }


    @Test
    public void findByTypeTest() {

        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());

        Task createdTask1 = taskStatusService.createOrUpdate(task1);
        Task createdTask2 = taskStatusService.createOrUpdate(task2);
        Task createdTask3 = taskStatusService.createOrUpdate(task3);

        Page<Task> tasksImportData = taskStatusService.findByType(TaskType.IMPORT_DATA.name(), 0, 5);
        Page<Task> tasksUploadFile = taskStatusService.findByType(TaskType.UPLOAD_FILE.name(), 0, 5);
        assertEquals(2, tasksImportData.getTotalElements());
        assertEquals(1, tasksUploadFile.getTotalElements());

        taskStatusService.delete(createdTask1.getId());
        taskStatusService.delete(createdTask2.getId());
        taskStatusService.delete(createdTask3.getId());
    }

    @Test
    public void findByUserIdAndTypeTest() {

        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.RELATIONSHIP_ENTITIES, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task4 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task5 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.ERROR, "Sad Error", new Date(), new Date());

        Task createdTask1 = taskStatusService.createOrUpdate(task1);
        Task createdTask2 = taskStatusService.createOrUpdate(task2);
        Task createdTask3 = taskStatusService.createOrUpdate(task3);
        Task createdTask4 = taskStatusService.createOrUpdate(task4);
        Task createdTask5 = taskStatusService.createOrUpdate(task5);

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

        taskStatusService.delete(createdTask1.getId());
        taskStatusService.delete(createdTask2.getId());
        taskStatusService.delete(createdTask3.getId());
        taskStatusService.delete(createdTask4.getId());
        taskStatusService.delete(createdTask5.getId());
    }

    @Test
    public void findByUserIdAndStatusTest() {

        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.RELATIONSHIP_ENTITIES, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task4 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task5 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.ERROR, "Sad Error", new Date(), new Date());

        Task createdTask1 = taskStatusService.createOrUpdate(task1);
        Task createdTask2 = taskStatusService.createOrUpdate(task2);
        Task createdTask3 = taskStatusService.createOrUpdate(task3);
        Task createdTask4 = taskStatusService.createOrUpdate(task4);
        Task createdTask5 = taskStatusService.createOrUpdate(task5);

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

        taskStatusService.delete(createdTask1.getId());
        taskStatusService.delete(createdTask2.getId());
        taskStatusService.delete(createdTask3.getId());
        taskStatusService.delete(createdTask4.getId());
        taskStatusService.delete(createdTask5.getId());
    }

    @Test
    public void findByUserIdAndTypeAndStatusTest() {
        Task task1 = new Task("user1", "Task1", TaskType.IMPORT_DATA, TaskStatus.PROCESSING, "Sad Error", new Date(), new Date());
        Task task2 = new Task("user1", "Task1", TaskType.RELATIONSHIP_ENTITIES, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task3 = new Task("user2", "Task1", TaskType.IMPORT_DATA, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task4 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.DONE, "Sad Error", new Date(), new Date());
        Task task5 = new Task("user2", "Task1", TaskType.UPLOAD_FILE, TaskStatus.ERROR, "Sad Error", new Date(), new Date());

        Task createdTask1 = taskStatusService.createOrUpdate(task1);
        Task createdTask2 = taskStatusService.createOrUpdate(task2);
        Task createdTask3 = taskStatusService.createOrUpdate(task3);
        Task createdTask4 = taskStatusService.createOrUpdate(task4);
        Task createdTask5 = taskStatusService.createOrUpdate(task5);

        Page<Task> tasksImportDataUser1 = taskStatusService.findByUserIdAndTypeAndStatus(
                task1.getUserId(), TaskType.IMPORT_DATA.name(), TaskStatus.PROCESSING.name(), 0, 5
        );
        Page<Task> tasksRelationshipUser2 = taskStatusService.findByUserIdAndTypeAndStatus(
                task3.getUserId(), TaskType.IMPORT_DATA.name(), TaskStatus.DONE.name(), 0, 5
        );

        assertEquals(1, tasksImportDataUser1.getTotalElements());
        assertEquals(1, tasksRelationshipUser2.getTotalElements());

        taskStatusService.delete(createdTask1.getId());
        taskStatusService.delete(createdTask2.getId());
        taskStatusService.delete(createdTask3.getId());
        taskStatusService.delete(createdTask4.getId());
        taskStatusService.delete(createdTask5.getId());
    }

    @Override
    protected void close() {
        super.close();
    }
}

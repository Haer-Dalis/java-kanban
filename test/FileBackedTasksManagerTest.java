import exception.ManagerSaveException;
import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.NEW;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private Path tempFilePath;
    private FileBackedTasksManager fileBackedTasksManager1;
    private FileBackedTasksManager fileBackedTasksManager2;
    private Task genericTask;

    @BeforeEach
    void setUp() throws IOException {
        tempFilePath = Files.createTempFile("test-", ".txt");
        taskManager = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
        genericTask = new Task("Test addNewTask", "Test addNewTask description",
                NEW, LocalDateTime.of(2024, 7, 2, 2, 5), Duration.ofHours(7));
        taskManager.addTask(genericTask);
    }

    @Test
    void saveAndLoadSeveralTasks() throws ManagerSaveException {
        fileBackedTasksManager1 = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
        fileBackedTasksManager2 = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
        Task task1 = new Task("First Task", "Description 1", Status.NEW,
                LocalDateTime.of(2024, 8, 2, 2, 5), Duration.ofHours(7));
        Task task2 = new Task("Second Task", "Description 2", Status.IN_PROGRESS,
                LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        fileBackedTasksManager1.addTask(task1);
        fileBackedTasksManager1.addTask(task2);
        fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(new File(String.valueOf(tempFilePath)));
        assertEquals(task1, fileBackedTasksManager2.getTask(1));
        assertEquals(task2, fileBackedTasksManager2.getTask(2));
    }

    @Test
    void saveAndLoadSeveralEpicsAndSubtasks() throws ManagerSaveException {
        fileBackedTasksManager1 = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
        fileBackedTasksManager2 = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
        Epic epic = new Epic("First epic", "Its description");
        Subtask subtask = new Subtask("First subtask", "Its description 2", Status.NEW, 1,
                LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        fileBackedTasksManager1.addEpic(epic);
        fileBackedTasksManager1.addSubtask(subtask);
        fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(new File(String.valueOf(tempFilePath)));
        assertEquals(epic, fileBackedTasksManager2.getEpic(1));
        assertEquals(subtask, fileBackedTasksManager2.getSubtask(2));
    }
}
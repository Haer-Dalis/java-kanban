import exception.ManagerSaveException;
import manager.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {

    private Path tempFilePath;
    private FileBackedTasksManager fileBackedTasksManager1;
    private FileBackedTasksManager fileBackedTasksManager2;

    @BeforeEach
    void setUp() throws IOException {
        tempFilePath = Files.createTempFile("test-", ".txt");
        fileBackedTasksManager1 = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
        fileBackedTasksManager2 = new FileBackedTasksManager(new File(String.valueOf(tempFilePath)));
    }

    @Test
    void saveAndLoadSeveralTasks() throws ManagerSaveException {
        Task task1 = new Task("First Task", "Description 1", Status.NEW);
        Task task2 = new Task("Second Task", "Description 2", Status.IN_PROGRESS);
        fileBackedTasksManager1.addTask(task1);
        fileBackedTasksManager1.addTask(task2);
        fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(new File(String.valueOf(tempFilePath)));
        assertEquals(task1, fileBackedTasksManager2.getTask(1));
        assertEquals(task2, fileBackedTasksManager2.getTask(2));
    }

    @Test
    void saveAndLoadSeveralEpicsAndSubtasks() throws ManagerSaveException {
        Epic epic = new Epic("First epic", "Its description");
        Subtask subtask = new Subtask("First subtask", "Its description 2", Status.NEW, 1);
        fileBackedTasksManager1.addEpic(epic);
        fileBackedTasksManager1.addSubtask(subtask);
        fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(new File(String.valueOf(tempFilePath)));
        assertEquals(epic, fileBackedTasksManager2.getEpic(1));
        assertEquals(subtask, fileBackedTasksManager2.getSubtask(2));
    }
}
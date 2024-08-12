import exception.ManagerSaveException;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import static task.Status.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private Task genericTask;
    private int taskId;

    @BeforeEach
    void setUp() throws ManagerSaveException {
        taskManager = new InMemoryTaskManager();
        genericTask = new Task("Test addNewTask", "Test addNewTask description",
                NEW, LocalDateTime.of(2024, 7, 2, 2, 5), Duration.ofHours(7));
        taskId = taskManager.addTask(genericTask);
    }

}

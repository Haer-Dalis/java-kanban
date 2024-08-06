import exception.ManagerSaveException;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("Добавление нового задания")
    protected void addTaskCheck() throws ManagerSaveException {
        super.addTaskCheck();
    }

    @Test
    @DisplayName("обновление задания")
    protected void updateTaskCheck() throws ManagerSaveException {
        super.updateTaskCheck();
    }

    @Test
    @DisplayName("получение задания")
    protected void getTaskCheck() throws ManagerSaveException {
        super.getTaskCheck();
    }

    @Test
    @DisplayName("удаление задачи")
    protected void deleteTaskCheck() throws ManagerSaveException {
        super.deleteTaskCheck();
    }

    @Test
    @DisplayName("Добавление эпика")
    protected void addEpicCheck() throws ManagerSaveException {
        super.addEpicCheck();
    }

    @Test
    @DisplayName("Получение эпика")
    protected void getEpicCheck() throws ManagerSaveException {
        super.getEpicCheck();
    }

    @Test
    public void addNewTask() throws ManagerSaveException {
        super.addNewTask();
    }

    @Test
    public void testTaskEqualityById() throws ManagerSaveException {
        super.testTaskEqualityById();
    }

    @Test
    public void testManagersAlwaysReturnExemplarsWhichAreReady() throws ManagerSaveException {
        super.testManagersAlwaysReturnExemplarsWhichAreReady();
    }

    @Test
    public void testEpicSubtaskEqualityById() throws ManagerSaveException {
        super.testEpicSubtaskEqualityById();
    }

    @Test
    public void testTaskManagerAddsTasksOfDifferentTypesAndCanFindThemById() throws ManagerSaveException {
        super.testTaskManagerAddsTasksOfDifferentTypesAndCanFindThemById();
    }

    @Test
    public void testGenegtedIdAndDesignatedIdDontConflict() throws ManagerSaveException {
        super.testGenegtedIdAndDesignatedIdDontConflict();
    }

    @Test
    public void testHistoryManagerSavesPreviousVersionsOfTasks() throws ManagerSaveException {
        super.testHistoryManagerSavesPreviousVersionsOfTasks();
    }

    @Test
    public void deletedSubtasksShouldNotStoreOldIdsInside() throws ManagerSaveException {
        super.deletedSubtasksShouldNotStoreOldIdsInside();
    }

    @Test
    public void noIrrelevantSubtasksInEpic() throws ManagerSaveException {
        super.noIrrelevantSubtasksInEpic();
    }

    @Test
    @DisplayName("Тест пересечения интервалов")
    public void overlappingCheck() throws ManagerSaveException {
        super.overlappingCheck();
    }

    @Test
    @DisplayName("Тест статусов эпика")
    public void epicStatusCheck() throws ManagerSaveException {
        super.epicStatusCheck();
    }
}

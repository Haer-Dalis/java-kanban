import exception.ManagerSaveException;
import exception.TimeException;
import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
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
    @Override
    @DisplayName("обновление задания")
    protected void updateTaskCheck() throws ManagerSaveException {
        super.updateTaskCheck();
    }

    @Test
    @Override
    @DisplayName("получение задания")
    protected void getTaskCheck() throws ManagerSaveException {
        super.getTaskCheck();
    }

    @Test
    @Override
    @DisplayName("удаление задачи")
    protected void deleteTaskCheck() throws ManagerSaveException {
        super.deleteTaskCheck();
    }

    @Test
    @Override
    @DisplayName("Добавление эпика")
    protected void addEpicCheck() throws ManagerSaveException {
        super.addEpicCheck();
    }

    @Test
    @Override
    @DisplayName("Получение эпика")
    protected void getEpicCheck() throws ManagerSaveException {
        super.getEpicCheck();
    }

    private void addEpic() throws ManagerSaveException {
        Epic epic = new Epic("Эпик №1", "Описание первого эпика");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Подзадача №1","Описание подзадачи №1",
                Status.NEW, 2, LocalDateTime.of(2025, 4, 1, 2, 5), Duration.ofHours(5)));
        Subtask subtask2 = new Subtask("Подзадача №2","Описание подзадачи №2",
                Status.NEW, 2, LocalDateTime.of(2024, 4, 1, 2, 5), Duration.ofHours(5));
        taskManager.addSubtask(subtask2);
    }

    @Test
    public void addNewTask() throws ManagerSaveException {
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(genericTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(genericTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testTaskEqualityById() throws ManagerSaveException {
        Task taskOne = taskManager.getTask(1);
        Task taskTwo = taskManager.getTask(1);
        System.out.println("Сравниваем задачи: " + taskOne.equals(taskTwo));
    }

    @Test
    public void testManagersAlwaysReturnExemplarsWhichAreReady() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager.getTaskList());
        historyManager.add(genericTask);
        assertNotNull(historyManager.getHistory());
    }

    @Test
    public void testEpicSubtaskEqualityById() throws ManagerSaveException {
        taskManager.addEpic(new Epic("Написать программу", "Непреодолимое дело"));
        Epic epicOne = taskManager.getEpic(2);
        Epic epicTwo = taskManager.getEpic(2);
        System.out.println("Сравниваем эпики: " + epicOne.equals(epicTwo));
        taskManager.addSubtask(new Subtask("Прочитать техзадание", "От этого многое зависит", Status.DONE,
                2, LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7)));
        Subtask subtaskOne = taskManager.getSubtask(3);
        Subtask subtaskTwo = taskManager.getSubtask(3);
        System.out.println("Сравниваем подзадачи: " + subtaskOne.equals(subtaskTwo));
    }

    @Test
    public void testTaskManagerAddsTasksOfDifferentTypesAndCanFindThemById() throws ManagerSaveException {
        Epic epic = new Epic("Написать программу", "Непреодолимое дело");
        Subtask subtask = new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS,
                2, LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        System.out.println("Сравниваем полученные задачи " + genericTask.equals(taskManager.getTask(1)));
        System.out.println("Сравниваем полученные эпики " + epic.equals(taskManager.getEpic(2)));
        System.out.println("Сравниваем полученные подзадачи " + subtask.equals(taskManager.getSubtask(3)));
    }

    @Test
    public void testGenegtedIdAndDesignatedIdDontConflict() throws ManagerSaveException {
        taskManager.addTask(new Task("Написать пункты расписания", "Не самое основное дело",
                Status.IN_PROGRESS, LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7)));
        taskManager.getTask(2).setId(2);
        assertEquals(2, taskManager.getTaskList().size());
    }

    @Test
    public void testTaskDoesntChange() throws ManagerSaveException {
        System.out.println("Сравниваем задачу после добавления: " + genericTask.equals(taskManager.getTask(1)));

    }

    @Test
    public void testHistoryManagerSavesPreviousVersionsOfTasks() throws ManagerSaveException {
        taskManager.getTask(1);
        Task newTask = new Task("Написать пункты расписания", "Не самое основное дело",
                Status.IN_PROGRESS, LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        newTask.setId(1);
        taskManager.updateTask(newTask);
        assertEquals(genericTask, taskManager.getHistory().get(0));
    }

    @Test
    public void deletedSubtasksShouldNotStoreOldIdsInside() throws ManagerSaveException {
        Epic epic = new Epic("Написать программу", "Непреодолимое дело");
        Subtask subtask = new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS, 2,
                LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtask(3);
        assertEquals(Collections.emptyList(), taskManager.getSubtaskList());
    }

    @Test
    public void noIrrelevantSubtasksInEpic() throws ManagerSaveException {
        Epic epic = new Epic("Написать программу", "Непреодолимое дело");
        Subtask subtask = new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS, 2,
                LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.deleteSubtask(3);
        assertEquals(Collections.emptyList(), taskManager.getEpic(2).getSubtasksOfEpic());
    }

    @Test
    @DisplayName("Тест пересечения интервалов")
    public void overlappingCheck() throws ManagerSaveException {
        TimeException ex = Assertions.assertThrows(TimeException.class, () -> taskManager.addTask(
                new Task("Написать новые пункты расписания", "Не самое основное дело",
                        Status.IN_PROGRESS, LocalDateTime.of(2024, 7, 2, 2, 5), Duration.ofHours(7))));
        Assertions.assertEquals("Нельзя выполнять два задания одновременно", ex.getMessage());

        taskManager.addTask(new Task("Ещё одна задача", "Описание другой задачи",
                Status.NEW, LocalDateTime.of(2025, 7, 2, 2, 5), Duration.ofHours(7)));
        TreeSet<Task> tasks = taskManager.getPrioritizedTasks();
        Assertions.assertTrue(tasks.first().getStartTime().isBefore(tasks.last().getStartTime()));
    }

    @Test
    @DisplayName("Тест статусов эпика")
    public void epicStatusCheck() throws ManagerSaveException {
        addEpic();
        Assertions.assertEquals(NEW, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
        taskManager.getSubtask(4).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtask(4));
        Assertions.assertEquals(IN_PROGRESS, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
        taskManager.getSubtask(3).setStatus(Status.DONE);
        taskManager.updateSubtask(taskManager.getSubtask(3));
        Assertions.assertEquals(DONE, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
        taskManager.getSubtask(3).setStatus(IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtask(3));
        taskManager.getSubtask(4).setStatus(IN_PROGRESS);
        taskManager.updateSubtask(taskManager.getSubtask(4));
        Assertions.assertEquals(IN_PROGRESS, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
    }



}

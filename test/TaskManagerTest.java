import exception.ManagerSaveException;
import exception.TaskManagerException;
import exception.TimeException;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import task.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static task.Status.*;
import static task.Status.IN_PROGRESS;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    private Task genericTask;
    private int taskId;

    @BeforeEach
    void setUp() throws IOException {
        genericTask = new Task("Test addNewTask", "Test addNewTask description",
                NEW, LocalDateTime.of(2024, 7, 2, 2, 5), Duration.ofHours(7));
        taskId = taskManager.addTask(genericTask);
    }

    private Task addTypicalTask() throws ManagerSaveException {
        Task task = new Task("Задача №1", "Описание задачи №1", Status.NEW, LocalDateTime.of(2020, 4, 1, 2, 5), Duration.ofHours(5));
        taskManager.addTask(task);
        return task;
    }

    private Epic addTypicalEpic() throws ManagerSaveException {
        Epic epic = new Epic("Эпик №1", "Описание первого эпика");
        taskManager.addEpic(epic);
        taskManager.addSubtask(new Subtask("Подзадача №1","Описание подзадачи №1",
                Status.NEW, 2, LocalDateTime.of(2023, 4, 1, 2, 5), Duration.ofHours(5)));
        taskManager.addSubtask(new Subtask("Подзадача №2","Описание подзадачи №2",
                Status.DONE, 2, LocalDateTime.of(2024, 4, 1, 2, 5), Duration.ofHours(5)));
        taskManager.addSubtask(new Subtask("Подзадача №3","Описание подзадачи №3",
                Status.IN_PROGRESS, 2, LocalDateTime.of(2025, 4, 1, 2, 5), Duration.ofHours(5)));
        return epic;
    }

    @Test
    @DisplayName("Добавление нового задания")
    protected void addTaskCheck() throws ManagerSaveException {
        Task task = new Task("Задача №1", "Описание задачи №1",
                Status.NEW, LocalDateTime.of(2023, 4, 1, 2, 5), Duration.ofHours(5));
        final int taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTask(taskId);
        assertNotNull(savedTask, "Задача не обнаружена");
        Assertions.assertEquals(task, savedTask, "Задачи не совпали");
        Assertions.assertNotNull(taskManager.getTaskList(), "Задачи не возвращаются");
        Assertions.assertEquals(2, taskManager.getTaskList().size(), "Неверное количество задач");
        Assertions.assertEquals(task, taskManager.getTaskList().get(1), "Задачи не совпадают");
    }

    @Test
    @DisplayName("обновление задания")
    protected void updateTaskCheck() throws ManagerSaveException {
        addTypicalTask();
        Task taskToUpdate = new Task("Задача для замены", "Описание задачи для замены",
                Status.DONE, LocalDateTime.of(2024, 4, 1, 2, 5), Duration.ofHours(5));
        taskToUpdate.setId(1);
        taskManager.updateTask(taskToUpdate);
        Assertions.assertEquals(taskToUpdate, taskManager.getTask(1), "Ошибка обновления задачи");
        Task task2 = new Task("Задача №2", "Описание задачи №2",
                Status.NEW, LocalDateTime.of(2024, 4, 1, 2, 5), Duration.ofHours(5));
        TaskManagerException ex = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.updateTask(task2));
        Assertions.assertEquals("Такой задачи нет", ex.getMessage());
        taskManager.deleteAllTasks();
        TaskManagerException ex2 = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.updateTask(taskToUpdate));
        Assertions.assertEquals("Такой задачи нет", ex2.getMessage());
    }


    @Test
    @DisplayName("получение задания")
    protected void getTaskCheck() throws ManagerSaveException {
        Task task = addTypicalTask();
        Assertions.assertEquals(task, taskManager.getTask(2), "Задача и полученная задача не совпадают");
        TaskManagerException ex = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.getTask(3));
        Assertions.assertEquals("Такой задачи нет", ex.getMessage());
        taskManager.deleteAllTasks();
        TaskManagerException ex2 = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.getTask(1));
        Assertions.assertEquals("Такой задачи нет", ex2.getMessage());
    }

    @Test
    @DisplayName("удаление задачи")
    protected void deleteTaskCheck() throws ManagerSaveException {
        addTypicalTask();
        taskManager.deleteTask(1);
        TaskManagerException ex = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.getTask(1));
        Assertions.assertEquals("Такой задачи нет", ex.getMessage());
    }

    @Test
    @DisplayName("Добавление эпика")
    protected void addEpicCheck() throws ManagerSaveException {
        addTypicalEpic();
        Assertions.assertNotNull(taskManager.getEpicList(), "Список эпиков почему-то пуст");
        Assertions.assertNotNull(taskManager.getEpic(2), "Созданный эпик не найден");
        Assertions.assertEquals(1, taskManager.getEpicList().size(), "Эпиков неверное количество");
        System.out.println(taskManager.getEpic(2));
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
        Assertions.assertEquals(3, taskManager.getSubtasksFromEpic(2).size(), "Неверное количество подзадач");
        taskManager.deleteSubtask(3);
        taskManager.deleteSubtask(5);
        Assertions.assertEquals(Status.DONE, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
    }

    @Test
    @DisplayName("Получение эпика")
    protected void getEpicCheck() throws ManagerSaveException {
        Epic epic = addTypicalEpic();
        Assertions.assertEquals(epic, taskManager.getEpic(2), "Эпик и полученный эпик не совпадают");
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(2).getStatus(), "Неверный статус эпика");
        TaskManagerException ex = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.getEpic(3));
        Assertions.assertEquals("Такого эпика нет и никогда не было", ex.getMessage());
        taskManager.deleteAllEpics();
        TaskManagerException ex2 = Assertions.assertThrows(TaskManagerException.class, () -> taskManager.getEpic(2));
        Assertions.assertEquals("Такого эпика нет и никогда не было", ex2.getMessage());
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
        final Task savedTask = taskManager.getTask(1);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(taskManager.getTask(1), savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testTaskEqualityById() throws ManagerSaveException {
        Task taskOne = taskManager.getTask(1);
        Task taskTwo = taskManager.getTask(1);
        System.out.println("Сравниваем задачи: " + taskOne.equals(taskTwo));
    }

    @Test
    public void testManagersAlwaysReturnExemplarsWhichAreReady() throws ManagerSaveException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(taskManager.getTaskList());
        historyManager.add(taskManager.getTask(1));
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
    public void testHistoryManagerSavesPreviousVersionsOfTasks() throws ManagerSaveException {
        Task task = taskManager.getTask(1);
        Task newTask = new Task("Написать пункты расписания", "Не самое основное дело",
                Status.IN_PROGRESS, LocalDateTime.of(2025, 8, 2, 2, 5), Duration.ofHours(7));
        newTask.setId(1);
        taskManager.updateTask(newTask);
        assertEquals(task, taskManager.getHistory().get(0));
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



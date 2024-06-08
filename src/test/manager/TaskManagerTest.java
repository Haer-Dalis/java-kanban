package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static task.Status.NEW;

class TaskManagerTest {
    private TaskManager taskManager;
    private Task genericTask;
    private int taskId;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        genericTask = new Task("Test addNewTask", "Test addNewTask description", NEW);
        taskId = taskManager.addTask(genericTask);
    }

    @Test
    void addNewTask() {
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(genericTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(genericTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testTaskEqualityById() {
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
    public void testEpicSubtaskEqualityById() {
        taskManager.addEpic(new Epic("Написать программу", "Непреодолимое дело"));
        Epic epicOne = taskManager.getEpic(2);
        Epic epicTwo = taskManager.getEpic(2);
        System.out.println("Сравниваем эпики: " + epicOne.equals(epicTwo));
        taskManager.addSubtask(new Subtask("Прочитать техзадание", "От этого многое зависит", Status.DONE, 2));
        Subtask subtaskOne = taskManager.getSubtask(3);
        Subtask subtaskTwo = taskManager.getSubtask(3);
        System.out.println("Сравниваем подзадачи: " + subtaskOne.equals(subtaskTwo));
    }

    @Test
    public void testTaskManagerAddsTasksOfDifferentTypesAndCanFindThemById() {
        Epic epic = new Epic("Написать программу", "Непреодолимое дело");
        Subtask subtask = new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS, 2);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        System.out.println("Сравниваем полученные задачи " + genericTask.equals(taskManager.getTask(1)));
        System.out.println("Сравниваем полученные эпики " + epic.equals(taskManager.getEpic(2)));
        System.out.println("Сравниваем полученные подзадачи " + subtask.equals(taskManager.getSubtask(3)));
    }

    @Test
    public void testGenegtedIdAndDesignatedIdDontConflict() {
        taskManager.addTask(new Task("Написать пункты расписания", "Не самое основное дело", Status.IN_PROGRESS));
        taskManager.getTask(2).setId(2);
        assertEquals(2, taskManager.getTaskList().size());
    }

    @Test
    public void testTaskDoesntChange() {
        System.out.println("Сравниваем задачу после добавления: " + genericTask.equals(taskManager.getTask(1)));

    }

    @Test
    public void testHistoryManagerSavesPreviousVersionsOfTasks() {
        taskManager.getTask(1);
        Task newTask = new Task("Написать пункты расписания", "Не самое основное дело", Status.IN_PROGRESS);
        newTask.setId(1);
        taskManager.updateTask(newTask);
        assertEquals(genericTask, taskManager.getHistory().get(0));
    }
}
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

    @BeforeEach
    void setUp() {
        TaskManager taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void testTaskEqualityById() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(new Task("Написать расписание", "Самое основное дело", NEW));
        Task taskOne = taskManager.getTask(1);
        Task taskTwo = taskManager.getTask(1);
        System.out.println("Сравниваем задачи: " + taskOne.equals(taskTwo));
    }

    @Test
    public void testManagersAlwaysReturnExemplarsWhichAreReady() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Написать расписание", "Самое основное дело", NEW);
        taskManager.addTask(task);
        assertNotNull(taskManager.getTaskList());
        historyManager.add(task);
        assertNotNull(historyManager.getHistory());
    }

    @Test
    public void testEpicSubtaskEqualityById() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addEpic(new Epic("Написать программу", "Непреодолимое дело"));
        Epic epicOne = taskManager.getEpic(1);
        Epic epicTwo = taskManager.getEpic(1);
        System.out.println("Сравниваем эпики: " + epicOne.equals(epicTwo));
        taskManager.addSubtask(new Subtask("Прочитать техзадание", "От этого многое зависит", Status.DONE, 1));
        Subtask subtaskOne = taskManager.getSubtask(2);
        Subtask subtaskTwo = taskManager.getSubtask(2);
        System.out.println("Сравниваем подзадачи: " + subtaskOne.equals(subtaskTwo));
    }

    @Test
    public void testTaskManagerAddsTasksOfDifferentTypesAndCanFindThemById() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Написать расписание", "Самое основное дело", NEW);
        Epic epic = new Epic("Написать программу", "Непреодолимое дело");
        Subtask subtask = new Subtask("Пробовать писать код", "Это сложно", Status.IN_PROGRESS, 2);
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        System.out.println("Сравниваем полученные задачи " + task.equals(taskManager.getTask(1)));
        System.out.println("Сравниваем полученные эпики " + epic.equals(taskManager.getEpic(2)));
        System.out.println("Сравниваем полученные подзадачи " + subtask.equals(taskManager.getSubtask(3)));
    }

    @Test
    public void testGenegtedIdAndDesignatedIdDontConflict() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(new Task("Написать расписание", "Самое основное дело", NEW));
        taskManager.addTask(new Task("Написать пункты расписания", "Не самое основное дело", Status.IN_PROGRESS));
        taskManager.getTask(2).setId(2);
        assertEquals(2, taskManager.getTaskList().size());
    }

    @Test
    public void testTaskDoesntChange() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Написать расписание", "Самое основное дело", NEW);
        taskManager.addTask(task);
        System.out.println("Сравниваем задачу после добавления: " + task.equals(taskManager.getTask(1)));

    }

    @Test
    public void testHistoryManagerSavesPreviousVersionsOfTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task oldTask = new Task("Написать расписание", "Самое основное дело", NEW);
        taskManager.addTask(oldTask);
        taskManager.getTask(1);
        Task newTask = new Task("Написать пункты расписания", "Не самое основное дело", Status.IN_PROGRESS);
        newTask.setId(1);
        taskManager.updateTask(newTask);
        assertEquals(oldTask, taskManager.getHistory().get(0));
    }
}
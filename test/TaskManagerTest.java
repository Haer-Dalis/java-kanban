import exception.ManagerSaveException;
import exception.TaskManagerException;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

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
        Task task = new Task("Задача №1", "Описание задачи №1", Status.NEW, LocalDateTime.of(2023, 4, 1, 2, 5), Duration.ofHours(5));
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


}



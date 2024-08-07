package manager;

import exception.ManagerSaveException;
import task.*;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    Integer addTask(Task task) throws ManagerSaveException;

    Integer addEpic(Epic epic) throws ManagerSaveException;

    Integer addSubtask(Subtask subtask) throws ManagerSaveException;

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    void deleteAllTasks() throws ManagerSaveException;

    void deleteAllEpics() throws ManagerSaveException;

    void deleteAllSubtasks() throws ManagerSaveException;

    Task getTask(int id) throws ManagerSaveException;

    Epic getEpic(int id) throws ManagerSaveException;

    Subtask getSubtask(int id) throws ManagerSaveException;

    void deleteTask(int id) throws ManagerSaveException;

    void deleteEpic(int id) throws ManagerSaveException;

    void deleteSubtask(int id) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    void updateSubtask(Subtask subtask) throws ManagerSaveException;

    List<Subtask> getSubtasksFromEpic(int id);
}


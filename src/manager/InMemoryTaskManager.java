package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int counter;


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Integer addTask(Task task) {
        task.setId(++counter);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Integer addEpic(Epic epic) {
        epic.setId(++counter);
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++counter);
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addEpicSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            return subtask.getId();
        } else {
            return null;
        }
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksOfEpic()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            epics.get(epicId).deleteEpicSubtask(id);
            updateEpicStatus(epicId);
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public List<Subtask> getSubtasksFromEpic (int id) {
        List<Subtask> subtasksFromEpic = new ArrayList<>();
        for (int i : epics.get(id).getSubtasksOfEpic()) {
            subtasksFromEpic.add(subtasks.get(i));
        }
        return subtasksFromEpic;
    }

    private void updateEpicStatus(int epicId) {
        Epic currentEpic = epics.get(epicId);
        if (currentEpic != null) {
            int completeSubtasks = 0;
            int newSubtasks = 0;
            if (currentEpic.getSubtasksOfEpic().isEmpty()) {
                currentEpic.setStatus(Status.NEW);
                return;
            } else {
                for (int subtaskId : currentEpic.getSubtasksOfEpic()) {
                    if (subtasks.get(subtaskId).getStatus().equals(Status.NEW)) {
                        newSubtasks++;
                    } else if (subtasks.get(subtaskId).getStatus().equals(Status.DONE)) {
                        completeSubtasks++;
                    } else if (subtasks.get(subtaskId).getStatus().equals(Status.IN_PROGRESS)) {
                        epics.get(epicId).setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
            }
            if (newSubtasks == currentEpic.getSubtasksOfEpic().size()) {
                currentEpic.setStatus(Status.NEW);
            } else if (completeSubtasks == currentEpic.getSubtasksOfEpic().size()) {
                currentEpic.setStatus(Status.DONE);
            } else {
                currentEpic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (int id : epics.keySet()) {
            Epic epic = epics.get(id);
            epic.deleteAllEpicSubtasks();
            epic.setStatus(Status.NEW);
        }
    }
}

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counter;

    public void addTask(Task task) {
        task.setId(++counter);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(++counter);
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++counter);
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addEpicSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksOfEpic()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            subtasks.remove(id);
            epics.get(epicId).deleteEpicSubtask(id);
            updateEpicStatus(epicId);
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public ArrayList<Subtask> getSubtasksFromEpic (int id) {
        ArrayList<Subtask> subtasksFromEpic = new ArrayList<>();
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

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (int id : epics.keySet()) {
            Epic epic = epics.get(id);
            epic.deleteAllEpicSubtasks();
            epic.setStatus(Status.NEW);
        }
    }
}

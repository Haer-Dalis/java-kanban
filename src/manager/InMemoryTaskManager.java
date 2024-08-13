package manager;

import exception.ManagerSaveException;
import exception.TaskManagerException;
import exception.TimeException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int counter;
    private final TreeSet<Task> sortedTasks = new TreeSet<>(new TimeOrderComparator());

    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Integer addTask(Task task) throws ManagerSaveException {
        task.setId(++counter);
        checkOverlapping(task);
        tasks.put(task.getId(), task);
        sortedTasks.add(task);
        return task.getId();
    }

    @Override
    public Integer addEpic(Epic epic) throws ManagerSaveException {
        epic.setId(++counter);
        epic.setStatus(Status.NEW);
        epics.put(epic.getId(), epic);
        updateEpicTime(epic.getId());
        return epic.getId();
    }

    @Override
    public Integer addSubtask(Subtask subtask) throws ManagerSaveException {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(++counter);
            checkOverlapping(subtask);
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addEpicSubtask(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
            updateEpicTime(subtask.getEpicId());
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
    public Task getTask(int id) throws ManagerSaveException {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.add(task);
            return tasks.get(id);
        } else throw new TaskManagerException("Такой задачи нет");
    }

    @Override
    public Epic getEpic(int id) throws ManagerSaveException {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.add(epic);
            return epics.get(id);
        } else throw new TaskManagerException("Такого эпика нет и никогда не было");
    }

    @Override
    public Subtask getSubtask(int id) throws ManagerSaveException {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        sortedTasks.remove(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        if (epics.containsKey(id)) {
            for (int subtaskId : epics.get(id).getSubtasksOfEpic()) {
                historyManager.remove(subtaskId);
                sortedTasks.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) throws ManagerSaveException {
        if (subtasks.containsKey(id)) {
            historyManager.remove(id);
            int epicId = subtasks.get(id).getEpicId();
            sortedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            epics.get(epicId).deleteEpicSubtask(id);
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        if (tasks.containsKey(task.getId())) {
            sortedTasks.remove(tasks.get(task.getId()));
            checkOverlapping(task);
            sortedTasks.add(task);
            tasks.put(task.getId(), task);
        } else throw new TaskManagerException("Такой задачи нет");
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicTime(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        if (subtasks.containsKey(subtask.getId()) && subtask.getEpicId() == subtasks.get(subtask.getId()).getEpicId()) {
            subtasks.put(subtask.getId(), subtask);
            sortedTasks.remove(subtasks.get(subtask.getId()));
            checkOverlapping(subtask);
            sortedTasks.add(subtask);
            updateEpicStatus(subtask.getEpicId());
            updateEpicTime(subtask.getEpicId());
        }
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int id) {
        List<Subtask> subtasksFromEpic = epics.get(id).getSubtasksOfEpic().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
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
    public void deleteAllTasks() throws ManagerSaveException {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() throws ManagerSaveException {
        subtasks.values().forEach(subtask -> {
            sortedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });

        subtasks.clear();

        epics.keySet().stream()
                .map(epics::get)
                .forEach(epic -> {
                    epic.deleteAllEpicSubtasks();
                    epic.setStatus(Status.NEW);
                    updateEpicTime(epic.getId());
                });
    }

    protected Integer addTaskWithId(Task task) {
        checkOverlapping(task);
        sortedTasks.add(task);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    protected Integer addEpicWithId(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic.getId());
        updateEpicStatus(epic.getId());
        return epic.getId();
    }

    protected Integer addSubtaskWithId(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        checkOverlapping(subtask);
        sortedTasks.add(subtask);
        epics.get(subtask.getEpicId()).addEpicSubtask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
        return subtask.getId();
    }

    protected void checkAttachIdsToEpics() {
        subtasks.values().stream()
                .filter(subtask -> epics.containsKey(subtask.getEpicId()) &&
                        !epics.get(subtask.getEpicId()).getSubtasksOfEpic().contains(subtask.getId()))
                .forEach(subtask -> {
                    epics.get(subtask.getEpicId()).addEpicSubtask(subtask.getId());
                    updateEpicStatus(subtask.getEpicId());
                });
    }

    protected void setCounterId(int counter) {
        this.counter = counter;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateEpicTime(Integer epicId) {
        List<Integer> subtasksOfEpic = epics.get(epicId).getSubtasksOfEpic();
        if (!subtasksOfEpic.isEmpty()) {
            LocalDateTime earliestTime = subtasksOfEpic.stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask.getStartTime() != null)
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime latestTime = subtasksOfEpic.stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask.getDuration() != null)
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Duration duration = subtasksOfEpic.stream()
                    .map(subtasks::get)
                    .filter(subtask -> subtask.getDuration() != null)
                    .map(Subtask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);

            epics.get(epicId).setStartTime(earliestTime);
            epics.get(epicId).setEndTime(latestTime);
            epics.get(epicId).setDuration(duration);
        } else {
            epics.get(epicId).setStartTime(null);
            epics.get(epicId).setEndTime(null);
            epics.get(epicId).setDuration(null);
        }
    }

    private void checkOverlapping(Task task) throws TimeException {
        boolean hasOverlap = getPrioritizedTasks().stream()
                .filter(task2 -> task2.getStartTime() != null && task2.getEndTime() != null)
                .anyMatch(task2 -> task.getStartTime() != null && task.getEndTime() != null &&
                        task.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task.getEndTime()));

        if (hasOverlap) {
            throw new TimeException("Нельзя выполнять два задания одновременно");
        }
    }

    class TimeOrderComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime() == null && o2.getStartTime() == null) return 1;
            else if (o1.getStartTime() == null) return 1;
            else if (o2.getStartTime() == null) return -1;
            else if (o1.getStartTime().isBefore(o2.getStartTime())) return -1;
            else if (o1.getStartTime().isAfter(o2.getStartTime())) return 1;
            else return 0;
        }
    }

    public TreeSet<Task> getSortedTasks() {
        return sortedTasks;
    }
}

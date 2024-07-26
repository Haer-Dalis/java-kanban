package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;
    private Formatter formatter = new Formatter();

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Integer addTask(Task task) throws ManagerSaveException {
        Integer id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public Integer addEpic(Epic epic) throws ManagerSaveException {
        Integer id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addSubtask(Subtask subtask) throws ManagerSaveException {
        Integer id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() throws ManagerSaveException {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTask(int id) throws ManagerSaveException {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) throws ManagerSaveException {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) throws ManagerSaveException {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTask(int id) throws ManagerSaveException {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) throws ManagerSaveException {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) throws ManagerSaveException {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
    }

    public FileBackedTasksManager load(File file) {
        int maxId = 0;
        List<String> lines = formatter.readFile(file);
        lines.remove(0);
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        for (String line : lines) {
            Task task = formatter.fromString(line);
            if (task.getId() > maxId) maxId = task.getId();
            if (task.getTaskType() == TaskType.TASK) {
                fileBackedTasksManager.addTaskWithId(task);
            } else if (task.getTaskType() == TaskType.EPIC) {
                fileBackedTasksManager.addEpicWithId((Epic) task);
            } else if (task.getTaskType() == TaskType.SUBTASK) {
                fileBackedTasksManager.addSubtaskWithId((Subtask) task);
            }
        }
        fileBackedTasksManager.checkAttachIdsToEpics();
        fileBackedTasksManager.setCounterId(maxId);
        return fileBackedTasksManager;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write("id,type,name,status,description,epic");
            bufferedWriter.write("\n");
            for (Task task : getTasks().values()) {
                bufferedWriter.write(formatter.taskToString(task));
                bufferedWriter.write("\n");
            }
            for (Epic epic : getEpics().values()) {
                bufferedWriter.write(formatter.epicToString(epic));
                bufferedWriter.write("\n");
            }
            for (Subtask subtask : getSubtasks().values()) {
                bufferedWriter.write(formatter.subtaskToString(subtask));
                bufferedWriter.write("\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при записи в файл");
        }
    }


}

package manager;

import task.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Formatter {
    protected Task fromString(String value) {
        String[] values = value.split(",");
        Integer id = Integer.valueOf(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String taskName = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        if (type == TaskType.TASK) {
            Task task = new Task(taskName, description, status);
            task.setId(id);
            return task;
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(taskName, description);
            epic.setId(id);
            return epic;
        } else if (type == TaskType.SUBTASK) {
            Integer epicId = Integer.valueOf(values[5]);
            Subtask subtask = new Subtask(taskName,description, status, epicId);
            subtask.setId(id);
            return subtask;
        }
        return null;
    }

    protected List<String> readFile(File file) {
        List<String> allLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while(reader.ready()) {
                line = reader.readLine();
                allLines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Произошла ошибка во время чтения файла.");
        }
        return allLines;
    }

    protected String taskToString(Task task) {
        return (task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription());
    }

    protected String epicToString(Epic epic) {
        return (epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() + "," + epic.getDescription());
    }

    protected String subtaskToString(Subtask subtask) {
        return (subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getName() + "," + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId());
    }

}
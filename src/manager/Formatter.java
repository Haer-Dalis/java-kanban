package manager;

import task.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Formatter {
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy; HH:mm");

    protected Task fromString(String value) {
        String[] values = value.split(",");
        Integer id = Integer.valueOf(values[0]);
        TaskType type = TaskType.valueOf(values[1]);
        String taskName = values[2];
        Status status = Status.valueOf(values[3]);
        String description = values[4];
        if (type == TaskType.TASK) {
            LocalDateTime startTime = LocalDateTime.parse(values[5], formatter);
            Duration duration = Duration.parse(values[6]);
            Task task = new Task(taskName, description, status, startTime, duration);
            task.setId(id);
            return task;
        } else if (type == TaskType.EPIC) {
            Epic epic = new Epic(taskName, description);
            epic.setId(id);
            return epic;
        } else if (type == TaskType.SUBTASK) {
            LocalDateTime startTime = LocalDateTime.parse(values[6], formatter);
            Duration duration = Duration.parse(values[7]);
            Integer epicId = Integer.valueOf(values[5]);
            Subtask subtask = new Subtask(taskName,description, status, epicId, startTime, duration);
            subtask.setId(id);
            return subtask;
        }
        return null;
    }

    protected List<String> readFile(File file) {
        List<String> allLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while (reader.ready()) {
                line = reader.readLine();
                allLines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Произошла ошибка во время чтения файла.");
        }
        return allLines;
    }

    protected String taskToString(Task task) {
        String startTimeString = task.getStartTime() != null ? task.getStartTime().format(formatter) : "";
        String durationString = task.getDuration() != null ? task.getDuration().toString() : "";
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + startTimeString + "," + durationString;
    }

    protected String epicToString(Epic epic) {
        return (epic.getId() + "," + epic.getTaskType() + "," + epic.getName() + "," + epic.getStatus() + ","
                + epic.getDescription());
    }

    protected String subtaskToString(Subtask subtask) {
        String startTimeString = subtask.getStartTime() != null ? subtask.getStartTime().format(formatter) : "";
        System.out.println(subtask.getStartTime().format(formatter));
        String durationString = subtask.getDuration() != null ? subtask.getDuration().toString() : "";
        return (subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicId() + "," + startTimeString + "," + durationString);
    }

}
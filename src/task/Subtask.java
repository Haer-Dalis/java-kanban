package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status, int epicId, LocalDateTime startTime, Duration duration)  {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Subtask otherTask = (Subtask) obj;
        return Objects.equals(getName(), otherTask.getName()) &&
                Objects.equals(getStatus(), otherTask.getStatus()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                getEpicId() == otherTask.getEpicId() &&
                (getId() == otherTask.getId());
    }
}
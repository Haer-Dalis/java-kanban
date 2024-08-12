package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksFromEpic = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getSubtasksOfEpic() {
        return new ArrayList<>(subtasksFromEpic);
    }

    public void addEpicSubtask(int id) {
        subtasksFromEpic.add(id);
    }

    public void deleteEpicSubtask(int id) {
        subtasksFromEpic.remove(Integer.valueOf(id));
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void deleteAllEpicSubtasks() {
        subtasksFromEpic.clear();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEpicEndTime() {
        return endTime;
    }


    @Override
    public String toString() {
        return "Epic{" +
                "subtasksFromEpic=" + subtasksFromEpic +
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
        Epic otherTask = (Epic) obj;
        return Objects.equals(getName(), otherTask.getName()) &&
                Objects.equals(getStatus(), otherTask.getStatus()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                Objects.equals(getSubtasksOfEpic(), otherTask.getSubtasksOfEpic()) &&
                (getId() == otherTask.getId());
    }
}

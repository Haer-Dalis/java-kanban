package task;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasksFromEpic = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasksOfEpic() {
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


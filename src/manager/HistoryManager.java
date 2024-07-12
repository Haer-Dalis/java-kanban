package manager;

import task.*;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);

    void remove(Integer id);
}
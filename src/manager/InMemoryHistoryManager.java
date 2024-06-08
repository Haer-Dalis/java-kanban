package manager;

import task.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.size() == MAX_HISTORY_SIZE) {
                history.removeFirst();
            }
            history.add(task);
        }
    }
}

package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.NEW;

class HistoryManagerTest {

    @BeforeEach
    void setUp() {
        HistoryManager historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(new Task("Написать расписание", "Самое основное дело", NEW));
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}
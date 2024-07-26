import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.IN_PROGRESS;
import static task.Status.NEW;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Test addNewTask", "Test addNewTask description", NEW);
        task2 = new Task("Test addNewTask 2", "Test addNewTask description 2", IN_PROGRESS);
        task1.setId(1);
        task2.setId(2);
        historyManager.add(task1);
        historyManager.add(task2);
    }

    @Test
    void add() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая.");
    }

    @Test
    void testAddAndRemoveTask() {
        assertEquals(2, historyManager.getHistory().size());
        historyManager.remove(1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void tasksWithTheSameIdSubstitute() {
        historyManager.remove(2);
        task2.setId(1);
        historyManager.add(task2);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void sequenceOfElementsIsCorrect() {
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
    }
}
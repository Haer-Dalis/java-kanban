import manager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.Status.IN_PROGRESS;
import static task.Status.NEW;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void start() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Test addNewTask", "Test addNewTask description",
                NEW, LocalDateTime.of(2024, 8, 2, 2, 5), Duration.ofHours(7));
        task2 = new Task("Test addNewTask 2", "Test addNewTask description 2",
                IN_PROGRESS, LocalDateTime.of(2028, 8, 2, 2, 5), Duration.ofHours(7));
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

    @Test
    @DisplayName("пустая история")
    protected void emptyHistoryTest() {
        historyManager.remove(1);
        historyManager.remove(2);
        NullPointerException ex = Assertions.assertThrows(NullPointerException.class, () -> historyManager.getHistory());
    }

    @Test
    @DisplayName("дублирование")
    protected void duplicateHistoryTest() {
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("удаление из начала, конца, середины")
    protected void deleteFromBeginningEndMiddleTest() {
        Task task3 = new Task("Test addNewTask 3", "Test addNewTask description 3",
                IN_PROGRESS, LocalDateTime.of(2029, 8, 2, 2, 5), Duration.ofHours(7));
        Task task4 = new Task("Test addNewTask 4", "Test addNewTask description 4",
                IN_PROGRESS, LocalDateTime.of(2036, 8, 2, 2, 5), Duration.ofHours(7));
        assertEquals(2, historyManager.getHistory().size());
        task3.setId(3);
        task4.setId(4);
        historyManager.add(task3);
        historyManager.add(task4);
        Assertions.assertEquals(Arrays.asList(task1, task2, task3, task4),
                historyManager.getHistory(), "ернулась не та последовательность задач");
        historyManager.remove(1);
        Assertions.assertEquals(Arrays.asList(task2, task3, task4),
                historyManager.getHistory(), "ернулась не та последовательность задач");
        historyManager.remove(3);
        Assertions.assertEquals(Arrays.asList(task2, task4),
                historyManager.getHistory(), "ернулась не та последовательность задач");
        historyManager.remove(4);
        Assertions.assertEquals(Arrays.asList(task2),
                historyManager.getHistory(), "ернулась не та последовательность задач");
    }

}
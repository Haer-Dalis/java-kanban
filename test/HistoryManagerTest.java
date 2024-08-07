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
    private Task task3;
    private Task task4;

    @BeforeEach
    void start() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Test addNewTask", "Test addNewTask description",
                NEW, LocalDateTime.of(2024, 8, 2, 2, 5), Duration.ofHours(7));
        task2 = new Task("Test addNewTask 2", "Test addNewTask description 2",
                IN_PROGRESS, LocalDateTime.of(2028, 8, 2, 2, 5), Duration.ofHours(7));
        task3 = new Task("Test addNewTask 3", "Test addNewTask description 3",
                IN_PROGRESS, LocalDateTime.of(2029, 8, 2, 2, 5), Duration.ofHours(7));
        task4 = new Task("Test addNewTask 4", "Test addNewTask description 4",
                IN_PROGRESS, LocalDateTime.of(2036, 8, 2, 2, 5), Duration.ofHours(7));
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
        task4.setId(4);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
    }

    @Test
    @DisplayName("Тест getHistory с граничными условиями")
    public void getHistoryTest() {
        Assertions.assertEquals(Arrays.asList(task1, task2, task3, task4),
                historyManager.getHistory(), "Вернулась не та последовательность задач");
        historyManager.remove(1);
        Assertions.assertEquals(Arrays.asList(task2, task3, task4),
                historyManager.getHistory(), "Вернулась не та последовательность задач");
        historyManager.remove(3);
        Assertions.assertEquals(Arrays.asList(task2, task4),
                historyManager.getHistory(), "Вернулась не та последовательность задач");
        historyManager.remove(4);
        Assertions.assertEquals(Arrays.asList(task2),
                historyManager.getHistory(), "Вернулась не та последовательность задач");
        historyManager.add(task2);
        assertEquals(task2, historyManager.getHistory().get(0), "произошло дублирование");
        assertEquals(1, historyManager.getHistory().size(), "произошло дублирование");
        historyManager.remove(2);
        assertEquals(List.of(), historyManager.getHistory(), "история задач не пуста");
    }

    @Test
    @DisplayName("Тест add с граничными условиями")
    public void addTest() {
        assertEquals(4, historyManager.getHistory().size(), "История должна содержать 1 задачу после добавления");
        assertEquals(task2, historyManager.getHistory().get(1), "некорректное добавление в историю");
        historyManager.add(task2);
        assertEquals(4, historyManager.getHistory().size(), "произошло дублирование");
        Assertions.assertEquals(Arrays.asList(task1, task3, task4, task2),
                historyManager.getHistory(), "Вернулась не та последовательность задач");

    }

    @Test
    @DisplayName("Тест remove")
    public void remove() {
        historyManager.remove(5);
        assertEquals(4, historyManager.getHistory().size(),
                "История должна содержать четыре задачи после попытки удаления несуществующей");
        historyManager.remove(1);
        historyManager.remove(3);
        historyManager.remove(4);
        Assertions.assertEquals(task2,
                historyManager.getHistory().get(0), "Не то значение было удалено");
        historyManager.remove(2);
        System.out.println(historyManager.getHistory());
        assertTrue(historyManager.getHistory().isEmpty(), "Удаление не привело к созданию пустого списка");
    }

    @Test
    @DisplayName("История пуста вначале")
    public void testInitialHistory() {
        HistoryManager historyManager2 = Managers.getDefaultHistory();
        List<Task> history = historyManager2.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пуста при инициализации");
    }

    @Test
    public void add() {
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(4, history.size(), "История не пустая.");
    }

    @Test
    public void testAddAndRemoveTask() {
        assertEquals(4, historyManager.getHistory().size());
        historyManager.remove(1);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    public void tasksWithTheSameIdSubstitute() {
        historyManager.remove(2);
        task2.setId(1);
        historyManager.add(task2);
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    public void sequenceOfElementsIsCorrect() {
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
    }

}
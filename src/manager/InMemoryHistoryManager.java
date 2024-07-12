package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> nodes = new HashMap<>();
    private Node last;
    private Node first;

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) { remove(task.getId());
        }
        linkLast(task);
        nodes.put(task.getId(), last);

    }

    @Override
    public void remove(Integer id) {
        Node node = nodes.get(id);
        if (node != null) {
            removeNode(node);
            nodes.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node.next == null && node.prev == null) { // Единственный элемент
            last = null;
            first = null;
        } else if (node.next == null) { // Последний элемент
            last = node.prev;
            last.next = null;
        } else if (node.prev == null) { // Первый элемент
            first = node.next;
            first.prev = null;
        } else { //Середина
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

    }


    private void linkLast(Task task) {
        Node newNode = new Node(task, last, null);
        if (first == null) {
            first = newNode;
        } else {
            last.next = newNode;
        }
        last = newNode;
    }

    private List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node node = first;
        while (true) {
            history.add(node.task);
            if (node.next != null) {
                node = node.next;
            } else {
                break;
            }
        }
        return new ArrayList<>(history);
    }
}

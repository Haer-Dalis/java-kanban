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
        if (task == null) {
            return;
        }
        if (nodes.containsKey(task.getId())) {
            remove(task.getId());
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
        if (node.getNext() == null && node.getPrev() == null) { // Единственный элемент
            last = null;
            first = null;
        } else if (node.getNext() == null) { // Последний элемент
            last = node.getPrev();
            last.setNext(null);
        } else if (node.getPrev() == null) { // Первый элемент
            first = node.getNext();
            first.setPrev(null);
        } else { //Середина
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }

    }

    private void linkLast(Task task) {
        Node newNode = new Node(task, last, null);
        if (first == null) {
            first = newNode;
        } else {
            last.setNext(newNode);
        }
        last = newNode;
    }

    private List<Task> getTasks() {
        if (first == null) {
            return new ArrayList<>();
        }
        List<Task> history = new ArrayList<>();
        Node node = first;
        while (true) {
            history.add(node.getTask());
            if (node.getNext() != null) {
                node = node.getNext();
            } else {
                break;
            }
        }
        return new ArrayList<>(history);
    }
}

package exception;

import java.util.NoSuchElementException;

public class TaskManagerException extends NoSuchElementException {
    public TaskManagerException(String message) {
        super(message);
    }
}
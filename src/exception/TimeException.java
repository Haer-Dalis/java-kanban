package exception;

import java.time.DateTimeException;

public class TimeException extends DateTimeException {
    public TimeException(String message) {
        super(message);
    }
}

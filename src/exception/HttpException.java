package exception;

import java.io.IOException;

public class HttpException extends IOException {
    public HttpException(String message) {
        super(message);
    }
}

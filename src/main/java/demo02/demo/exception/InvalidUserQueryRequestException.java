package demo02.demo.exception;

public class InvalidUserQueryRequestException extends RuntimeException {
    public InvalidUserQueryRequestException(String message) {
        super(message);
    }
}

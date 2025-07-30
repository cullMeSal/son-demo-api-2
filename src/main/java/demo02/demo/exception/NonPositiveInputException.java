package demo02.demo.exception;

public class NonPositiveInputException extends RuntimeException {
    public NonPositiveInputException(String message) {
        super(message);
    }
}

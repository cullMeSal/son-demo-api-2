package demo02.demo.exception;

public class UserQueryOutOfBoundException extends RuntimeException {
    public UserQueryOutOfBoundException(String message) {
        super(message);
    }
}

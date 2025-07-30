package demo02.demo.exception;

public class InvalidEmailFormatException extends RuntimeException{
    public InvalidEmailFormatException(String message) {super(message);}
}

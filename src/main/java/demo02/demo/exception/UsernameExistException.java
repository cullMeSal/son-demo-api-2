package demo02.demo.exception;

public class UsernameExistException extends Exception{
    public UsernameExistException(String message){
        super(message);
    }
}

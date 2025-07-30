package demo02.demo.controller;

import demo02.demo.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<?> handleUserAlreadyExist(UsernameAlreadyExistException e)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()+" [this is additional text]");
    }
    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<?> handleInvalidEmailFormat(InvalidEmailFormatException e)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMessageNotReadable(HttpMessageNotReadableException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON body format.");
    }
    @ExceptionHandler(DeniedUserInfoRequestException.class)
    public ResponseEntity<?> handleDeniedUserInfoRequest(DeniedUserInfoRequestException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
    @ExceptionHandler(UserQueryOutOfBoundException.class)
    public ResponseEntity<?> handleUserQueryOutOfBound(UserQueryOutOfBoundException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(NonPositiveInputException.class)
    public ResponseEntity<?> handleNonPositiveInput(NonPositiveInputException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(InvalidUserQueryRequestException.class)
    public ResponseEntity<?> handleInvalidUserQueryRequest(InvalidUserQueryRequestException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

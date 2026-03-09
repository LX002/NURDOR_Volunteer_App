package com.nurdor_project.donations_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotValidStandIdException.class)
    public ResponseEntity<ErrorEntity> handleNotValidStandIdException(NotValidStandIdException e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StandNotFoundException.class)
    public ResponseEntity<ErrorEntity> handleStandNotFoundException(StandNotFoundException e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughStandsException.class)
    public ResponseEntity<ErrorEntity> handleNotEnoughStandsException(NotEnoughStandsException e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotStartedEventException.class)
    public ResponseEntity<ErrorEntity> handleNotStartedEventException(NotStartedEventException e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEntity> handleException(Exception e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

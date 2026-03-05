package com.nurdor_project.donations_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

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
}

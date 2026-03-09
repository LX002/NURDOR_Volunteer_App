package com.nurdor_project.volunteer_service.exception;

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

    @ExceptionHandler(VolunteerNotFoundException.class)
    public ResponseEntity<ErrorEntity> handleVolunteerNotFoundException(VolunteerNotFoundException e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEntity> handleException(Exception e) {
        List<String> splitMessage = Arrays.stream(e.getMessage().split("\"")).map(String::toString).toList();
        if(!splitMessage.isEmpty()) {
            return new ResponseEntity<>(new ErrorEntity(splitMessage.get(3), LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}

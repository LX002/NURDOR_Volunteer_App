package com.nurdor_project.donations_service.exception;

public class NotStartedEventException extends RuntimeException {
    public NotStartedEventException(String message) {
        super(message);
    }
}

package com.nurdor_project.events_log_service.exception;

public class InvalidEventsLogException extends RuntimeException {
    public InvalidEventsLogException(String message) {
        super(message);
    }
}

package com.nurdor_project.donations_service.exception;

public class NotEnoughStandsException extends RuntimeException {
    public NotEnoughStandsException(String message) {
        super(message);
    }
}

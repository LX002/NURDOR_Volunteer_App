package com.nurdorproject.auth_service.exception;

public class VolunteerAlreadyExistsException extends RuntimeException {
    public VolunteerAlreadyExistsException(String message) {
        super(message);
    }
}

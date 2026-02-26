package com.nurdorproject.auth_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class RegisterDTO implements Serializable {
    @NotNull
    @Size(max = 100)
    private final String name;
    @NotNull
    @Size(max = 100)
    private final String surname;
    @NotNull
    @Size(max = 200)
    private final String address;
    @NotNull
    @Size(max = 20)
    private final String phoneNumber;
    @NotNull
    @Size(max = 100)
    private final String email;
    @NotNull
    @Size(max = 100)
    private final String username;
    @NotNull
    @Size(max = 500)
    private final String password;
    private final byte[] profilePicture;
    @NotNull
    private final String zipCode;
    private final String volunteerRole;
}
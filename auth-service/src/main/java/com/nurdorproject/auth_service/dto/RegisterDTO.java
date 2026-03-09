package com.nurdorproject.auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class RegisterDTO implements Serializable {
    @NotNull
    @NotBlank
    @Size(max = 100)
    private final String name;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private final String surname;

    @NotNull
    @NotBlank
    @Size(max = 200)
    private final String address;

    @NotNull
    @NotBlank
    @Size(max = 20)
    private final String phoneNumber;

    @NotNull
    @NotBlank
    @Email
    @Size(max = 100)
    private final String email;

    @NotNull
    @NotBlank
    @Size(max = 100)
    private final String username;

    @NotNull
    @NotBlank
    @Size(max = 500)
    private final String password;

    private final byte[] profilePicture;

    @NotNull
    private final String zipCode;

    @NotNull
    @Min(1) @Max(2)
    private final String volunteerRole;
}
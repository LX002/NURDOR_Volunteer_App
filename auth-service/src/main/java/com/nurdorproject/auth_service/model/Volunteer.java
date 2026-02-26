package com.nurdorproject.auth_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVolunteer", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 100)
    @NotNull
    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Size(max = 200)
    @NotNull
    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Size(max = 20)
    @NotNull
    @Column(name = "phoneNumber", nullable = false, length = 20)
    private String phoneNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 100)
    @NotNull
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Size(max = 500)
    @NotNull
    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @Column(name = "profilePicture")
    private byte[] profilePicture;

    @NotNull
    @Column(name = "nearestCity", nullable = false)
    private String nearestCity;

    @NotNull
    @Column(name = "volunteerRole", nullable = false)
    private Integer volunteerRole;


    public Volunteer(String name, String surname, String address, String phoneNumber, String email, String username, String password, byte[] profilePicture, String zipCode, int role) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
        this.nearestCity = zipCode;
        this.volunteerRole = role;
    }
}
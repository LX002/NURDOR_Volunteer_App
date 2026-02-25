package com.nurdorproject.api_gateway_v2.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVolunteer", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Size(max = 500)
    @NotNull
    @Column(name = "password", nullable = false, length = 500)
    private String password;

    @NotNull
    @Column(name = "volunteerRole", nullable = false)
    private Integer volunteerRole;


}
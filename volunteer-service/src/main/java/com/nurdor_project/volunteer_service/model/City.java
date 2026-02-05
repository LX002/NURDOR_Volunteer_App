package com.nurdor_project.volunteer_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "city")
public class City {
    @Id
    @Size(max = 10)
    @Column(name = "zipCode", nullable = false, length = 10)
    private String zipCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "cityName", nullable = false, length = 100)
    private String cityName;

    @OneToMany
    @JoinColumn(name = "nearestCity")
    private Set<Volunteer> volunteers = new LinkedHashSet<>();


}
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
@Table(name = "volunteer_role")
public class VolunteerRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVolunteerRole", nullable = false)
    private Integer id;

    @Size(max = 200)
    @NotNull
    @Column(name = "roleName", nullable = false, length = 200)
    private String roleName;

    @OneToMany
    @JoinColumn(name = "volunteerRole")
    private Set<Volunteer> volunteers = new LinkedHashSet<>();


}
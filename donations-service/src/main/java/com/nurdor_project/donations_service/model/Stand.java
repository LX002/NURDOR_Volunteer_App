package com.nurdor_project.donations_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stand")
public class Stand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idStand", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "standName", nullable = false, length = 100)
    private String standName;

    @NotNull
    @Column(name = "donations", nullable = false)
    private Integer donations;

    @Column(name = "event")
    private Integer idEvent;
}
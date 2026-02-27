package com.nurdorproject.event_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEvent", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "eventName", nullable = false, length = 100)
    private String eventName;

    @NotNull
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "startTime", nullable = false)
    private Instant startTime;

    @NotNull
    @Column(name = "endTime", nullable = false)
    private Instant endTime;

    @NotNull
    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @NotNull
    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Size(max = 200)
    @Column(name = "locationDesc", length = 200)
    private String locationDesc;

    @Column(name = "eventImg")
    private byte[] eventImg;

    @NotNull
    @Column(name = "city", nullable = false)
    private String city;


}
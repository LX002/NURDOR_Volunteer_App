package com.nurdor_project.events_log_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "events_log")
public class EventsLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEventsLog", nullable = false)
    private Integer id;

    @Min(1)
    @NotNull
    @Column(name = "volunteer", nullable = false)
    private Integer volunteer;

    @Min(1)
    @NotNull
    @Column(name = "event", nullable = false)
    private Integer event;

    @Min(0)
    @Max(1)
    @NotNull
    @Column(name = "isPresent", nullable = false)
    private Byte isPresent;

    @Lob
    @Column(name = "note")
    private String note;


    public EventsLog(Integer volunteer, Integer event, Byte isPresent, String note) {
        this.volunteer = volunteer;
        this.event = event;
        this.isPresent = isPresent;
        this.note = note;
    }
}
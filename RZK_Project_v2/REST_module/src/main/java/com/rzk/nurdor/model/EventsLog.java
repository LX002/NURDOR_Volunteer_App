package com.rzk.nurdor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events_log", schema = "rzk_rma_schema")
public class EventsLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEventsLog", nullable = false)
    private Integer id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "volunteer", nullable = false)
    private Volunteer volunteer;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "event", nullable = false)
    private Event event;

    @Column(name = "isPresent", nullable = false)
    private Byte isPresent;

    @Lob
    @Column(name = "note")
    private String note;

    public EventsLog() {  }

    public EventsLog(Integer id, Volunteer volunteer, Event event, Byte isPresent, String note) {
        this.id = id;
        this.volunteer = volunteer;
        this.event = event;
        this.isPresent = isPresent;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Byte getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Byte isPresent) {
        this.isPresent = isPresent;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
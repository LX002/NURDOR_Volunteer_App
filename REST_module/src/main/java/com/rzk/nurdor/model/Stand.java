package com.rzk.nurdor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stand", schema = "rzk_rma_schema")
public class Stand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idStand", nullable = false)
    private Integer id;

    @Column(name = "standName", nullable = false, length = 100)
    private String standName;

    @Column(name = "totalDonations", nullable = false)
    private Integer totalDonations;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "event", nullable = false)
    private Event event;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStandName() {
        return standName;
    }

    public void setStandName(String standName) {
        this.standName = standName;
    }

    public Integer getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(Integer totalDonations) {
        this.totalDonations = totalDonations;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
package com.rzk.nurdor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "volunteer", schema = "rzk_rma_schema")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVolunteer", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Column(name = "phoneNumber", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Column(name = "password", nullable = false, length = 45)
    private String password;

    @Lob
    @JoinColumn(name = "profilePicture", nullable = true)
    private byte[] profilePicture;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "nearestCity", nullable = false)
    private City nearestCity;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "volunteerRole", nullable = false)
    private VolunteerRole volunteerRole;

    @JsonIgnore
    @OneToMany(mappedBy = "volunteer")
    private Set<EventsLog> eventsLogs = new LinkedHashSet<>();

    public Volunteer() {

    }

    public Volunteer(Integer id, String name, String surname, String address, String phoneNumber, String email, String username, String password, byte[] profilePicture, City nearestCity, VolunteerRole volunteerRole) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profilePicture = profilePicture;
        this.nearestCity = nearestCity;
        this.volunteerRole = volunteerRole;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public City getNearestCity() {
        return nearestCity;
    }

    public void setNearestCity(City nearestCity) {
        this.nearestCity = nearestCity;
    }

    public VolunteerRole getVolunteerRole() {
        return volunteerRole;
    }

    public void setVolunteerRole(VolunteerRole volunteerRole) {
        this.volunteerRole = volunteerRole;
    }

    public Set<EventsLog> getEventsLogs() {
        return eventsLogs;
    }

    public void setEventsLogs(Set<EventsLog> eventsLogs) {
        this.eventsLogs = eventsLogs;
    }

    public byte[] getProfilePicture() {
        if(profilePicture == null) {
            return new byte[0];
        }
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}
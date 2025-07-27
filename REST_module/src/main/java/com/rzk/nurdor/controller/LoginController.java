package com.rzk.nurdor.controller;

import com.rzk.nurdor.dto.VolunteerDto;
import com.rzk.nurdor.dto.VolunteerExpandedDto;
import com.rzk.nurdor.model.City;
import com.rzk.nurdor.model.Volunteer;
import com.rzk.nurdor.model.VolunteerRole;
import com.rzk.nurdor.security.PasswordUtils;
import com.rzk.nurdor.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    //login sada na android strani!
    @PostMapping
    public ResponseEntity<Boolean> login(@RequestBody VolunteerDto volunteerDto) {
        System.out.println(PasswordUtils.hashPassword(volunteerDto.getPassword()));
        System.out.println(volunteerDto.getUsername() + " " + volunteerDto.getPassword());
        System.out.println(volunteerDto.toString() + " " + loginService.authenticate(volunteerDto));
        if(loginService.authenticate(volunteerDto)) {
            return ResponseEntity.ok(true);
        } else {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/saveVolunteer")
    public ResponseEntity<Boolean> saveVolunteer(@RequestBody VolunteerExpandedDto volunteerDto) {
        System.out.println(volunteerDto.getVolunteerRole() + ". role,  n. city: " + volunteerDto.getNearestCity());
        Volunteer newVolunteer = new Volunteer(
                volunteerDto.getId(),
                volunteerDto.getName(),
                volunteerDto.getSurname(),
                volunteerDto.getAddress(),
                volunteerDto.getPhoneNumber(),
                volunteerDto.getEmail(),
                volunteerDto.getUsername(),
                volunteerDto.getPassword(), //hash lozinke se desava na android strani!
                Base64.getDecoder().decode(volunteerDto.getProfilePicture()),
                loginService.findCityByZipCode(volunteerDto.getNearestCity()),
                loginService.findVolunteerRoleById(volunteerDto.getVolunteerRole())
        );
        return ResponseEntity.ok(loginService.saveVolunteer(newVolunteer) == null);
    }

    @GetMapping("/getCities")
    public ResponseEntity<List<City>> getCities() {
        List<City> cities = loginService.getCities();
        if(cities == null || cities.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/getRoles")
    public ResponseEntity<List<VolunteerRole>> getRoles() {
        List<VolunteerRole> roles = loginService.getRoles();
        if(roles == null || roles.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(roles);
    }
}

package com.nurdor_project.volunteer_service.controller;

import com.nurdor_project.volunteer_service.model.Volunteer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/welcome")
public class WelcomeController {

    @GetMapping
    public ResponseEntity<String> welcomeMessage(Authentication authentication) {
        Volunteer loggedIn = (Volunteer) authentication.getPrincipal();
        return ResponseEntity.ok("Welcome " + loggedIn.getName());
    }
}

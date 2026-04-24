package com.nurdorproject.auth_service.controller;

import com.nurdorproject.auth_service.dto.LoginDTO;
import com.nurdorproject.auth_service.dto.LoginResponse;
import com.nurdorproject.auth_service.dto.RegisterDTO;
import com.nurdorproject.auth_service.exception.VolunteerAlreadyExistsException;
import com.nurdorproject.auth_service.service.AuthServiceImpl;
import com.nurdorproject.auth_service.service.VolunteerDetailsService;
import com.nurdorproject.auth_service.utils.JwtUtil;
import com.nurdorproject.auth_service.utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private JwtUtil jwtUtil;
    private AuthServiceImpl authService;
    private AuthenticationManager authenticationManager;
    private VolunteerDetailsService volunteerDetailsService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        if(isExistingVolunteer(registerDTO)) {
            return ResponseHandler.generateResponse("Volunteer with same username / email / phone number already exists!", HttpStatus.CONFLICT, null);
        }

        System.out.println("Registering with password " + registerDTO.getPassword());

        // [NOTE TO SELF] you are assuming that everything goes well with saving... fix this?
        return ResponseHandler.generateResponse("Registration successful!", HttpStatus.OK, authService.register(registerDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            String username = loginDTO.getUsername();
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword()));
            UserDetails userDetails = volunteerDetailsService.loadUserByUsername(username);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);
            LoginResponse loginResponse = LoginResponse
                    .builder()
                    .volunteerId(volunteerDetailsService.findByUsername(username).getId())
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .build();
            return ResponseHandler.generateResponse("Volunteer logged in successfully!", HttpStatus.OK, loginResponse);
        } catch(Exception ex) {
            return ResponseHandler.generateResponse("Invalid credentials!", HttpStatus.BAD_REQUEST, null);
        }
    }

    private Boolean isExistingVolunteer(RegisterDTO registerDTO) {
        return volunteerDetailsService.findByEmail(registerDTO.getEmail()) != null
                || volunteerDetailsService.findByUsername(registerDTO.getUsername()) != null
                || volunteerDetailsService.findByPhoneNumber(registerDTO.getPhoneNumber()) != null;
    }
}

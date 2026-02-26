package com.nurdorproject.auth_service.controller;

import com.nurdorproject.auth_service.dto.LoginDTO;
import com.nurdorproject.auth_service.dto.LoginResponse;
import com.nurdorproject.auth_service.dto.RegisterDTO;
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

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private JwtUtil jwtUtil;
    private AuthServiceImpl authService;
    private AuthenticationManager authenticationManager;
    private VolunteerDetailsService volunteerDetailsService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return ResponseHandler.generateResponse("User registered successfully", HttpStatus.OK, authService.register(registerDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            UserDetails userDetails = volunteerDetailsService.loadUserByUsername(loginDTO.getUsername());
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            String jwt = jwtUtil.generateToken(userDetails.getUsername(), roles);
            LoginResponse loginResponse = LoginResponse
                    .builder()
                    .accessToken(jwt)
                    .build();
            return ResponseHandler.generateResponse("Volunteer logged in successfully!", HttpStatus.OK, loginResponse);
        } catch(Exception ex) {
            return new ResponseEntity<>("Incorrect credentials, try again!", HttpStatus.BAD_REQUEST);
        }
    }
}

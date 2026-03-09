package com.nurdorproject.auth_service.service;

import com.nurdorproject.auth_service.dto.RegisterDTO;
import com.nurdorproject.auth_service.dto.RegisterResponse;
import com.nurdorproject.auth_service.model.Volunteer;
import com.nurdorproject.auth_service.repository.VolunteerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private PasswordEncoder passwordEncoder;
    private VolunteerRepository volunteerRepository;

    @Override
    public RegisterResponse register(RegisterDTO registerDTO) {
        if(volunteerRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
            // replace this with new exception, error detail etc
            throw new RuntimeException("User with that username already exists!");
        } else {
            Volunteer newVol = new Volunteer(
                    registerDTO.getName(),
                    registerDTO.getSurname(),
                    registerDTO.getAddress(),
                    registerDTO.getPhoneNumber(),
                    registerDTO.getEmail(),
                    registerDTO.getUsername(),
                    passwordEncoder.encode(registerDTO.getPassword()),
                    registerDTO.getProfilePicture(),
                    registerDTO.getZipCode(),
                    registerDTO.getVolunteerRole().equals("ADMIN") ? 1 : 2
            );

            Volunteer saved = volunteerRepository.save(newVol);
            return RegisterResponse.builder()
                    .id(saved.getId())
                    .username(saved.getUsername())
                    .email(saved.getEmail())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .message("Volunteer saved successfuly!")
                    .build();
        }
    }
}

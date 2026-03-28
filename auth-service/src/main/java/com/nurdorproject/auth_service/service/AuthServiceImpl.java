package com.nurdorproject.auth_service.service;

import com.nurdorproject.auth_service.dto.RegisterDTO;
import com.nurdorproject.auth_service.dto.RegisterResponse;
import com.nurdorproject.auth_service.model.Volunteer;
import com.nurdorproject.auth_service.repository.VolunteerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

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
            String encodedPicture = registerDTO.getProfilePicture();
            byte[] picture = encodedPicture != null
                    ? Base64.getDecoder().decode(encodedPicture)
                    : null;

            Volunteer newVol = new Volunteer(
                    registerDTO.getName(),
                    registerDTO.getSurname(),
                    registerDTO.getAddress(),
                    registerDTO.getPhoneNumber(),
                    registerDTO.getEmail(),
                    registerDTO.getUsername(),
                    passwordEncoder.encode(registerDTO.getPassword()),
                    picture,
                    registerDTO.getZipCode(),
                    registerDTO.getVolunteerRole()
            );

            Volunteer saved = volunteerRepository.save(newVol);
            return RegisterResponse.builder()
                    .id(saved.getId())
                    .username(saved.getUsername())
                    .email(saved.getEmail())
                    .createdAt(LocalDateTime.now().toString())
                    .updatedAt(LocalDateTime.now().toString())
                    .message("Volunteer saved successfuly!")
                    .build();
        }
    }
}

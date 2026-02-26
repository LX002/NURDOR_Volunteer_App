package com.nurdorproject.auth_service.service;

import com.nurdorproject.auth_service.dto.RegisterDTO;
import com.nurdorproject.auth_service.dto.RegisterResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    RegisterResponse register(RegisterDTO registerDTO);
}

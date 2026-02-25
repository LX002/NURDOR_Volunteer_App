package com.nurdorproject.api_gateway_v2.service;

import com.nurdorproject.api_gateway_v2.dto.RegisterDTO;
import com.nurdorproject.api_gateway_v2.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse response(RegisterDTO registerDTO);
}

package com.nurdorproject.api_gateway_v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDTO {

    // add more fields and restrictions... look at the site you've picked this up from
    private String username;
    private String password;
    private String email;
}

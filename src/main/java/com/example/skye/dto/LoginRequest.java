package com.example.skye.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is mandatory")
    @Email(message = "Username must be a valid email address")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
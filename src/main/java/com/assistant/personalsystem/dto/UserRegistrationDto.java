package com.assistant.personalsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String fullName;
} 
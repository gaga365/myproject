package com.assistant.personalsystem.service;

import com.assistant.personalsystem.dto.UserRegistrationDto;
import com.assistant.personalsystem.model.User;

public interface UserService {
    
    User registerNewUser(UserRegistrationDto registrationDto) throws Exception;
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    boolean checkUsernameExists(String username);
    
    boolean checkEmailExists(String email);
} 
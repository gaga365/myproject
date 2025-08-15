package com.assistant.personalsystem.service.impl;

import com.assistant.personalsystem.dto.UserRegistrationDto;
import com.assistant.personalsystem.model.User;
import com.assistant.personalsystem.repository.UserRepository;
import com.assistant.personalsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public User registerNewUser(UserRegistrationDto registrationDto) throws Exception {
        // 验证两次密码是否一致
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new Exception("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (checkUsernameExists(registrationDto.getUsername())) {
            throw new Exception("该用户名已被使用");
        }
        
        // 检查邮箱是否已存在
        if (checkEmailExists(registrationDto.getEmail())) {
            throw new Exception("该邮箱已被注册");
        }
        
        // 创建新用户
        User user = User.builder()
                .username(registrationDto.getUsername())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .email(registrationDto.getEmail())
                .fullName(registrationDto.getFullName())
                .createdAt(LocalDateTime.now())
                .build();
        
        return userRepository.save(user);
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
} 
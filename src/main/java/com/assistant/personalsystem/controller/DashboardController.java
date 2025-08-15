package com.assistant.personalsystem.controller;

import com.assistant.personalsystem.model.User;
import com.assistant.personalsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    
    private final UserService userService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
        }
        model.addAttribute("user", user);
        return "dashboard";
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    // 为其他功能预留的控制器方法
    @GetMapping("/search")
    public String search() {
        return "search";
    }
} 
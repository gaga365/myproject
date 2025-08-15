package com.assistant.personalsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AIPageController {
    @GetMapping("/ai-translation")
    public String aiTranslationPage() {
        return "ai-translation";
    }
} 
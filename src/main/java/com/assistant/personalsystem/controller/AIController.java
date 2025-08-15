package com.assistant.personalsystem.controller;

import com.assistant.personalsystem.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    @PostMapping("/translate")
    public Map<String, Object> translate(@RequestBody Map<String, String> req) {
        String text = req.getOrDefault("text", "");
        String from = req.getOrDefault("from", "auto");
        String to = req.getOrDefault("to", "en");
        String result = aiService.translateWithDeepSeek(text, from, to);
        Map<String, Object> resp = new HashMap<>();
        resp.put("result", result);
        return resp;
    }
} 
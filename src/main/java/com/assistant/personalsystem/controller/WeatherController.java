package com.assistant.personalsystem.controller;

import com.assistant.personalsystem.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {
    
    private final WeatherService weatherService;
    
    @GetMapping
    public String weatherPage() {
        return "weather";
    }
    
    @GetMapping("/data")
    @ResponseBody
    public Map<String, String> getWeatherData(@RequestParam(defaultValue = "北京") String city) {
        return weatherService.getWeather(city);
    }
    
    @GetMapping("/fragment")
    public String weatherFragment() {
        return "weather-fragment";
    }
} 
package com.assistant.personalsystem.controller;

import com.assistant.personalsystem.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping
    public String listSchedules(Model model) {
        try {
            log.debug("Fetching all schedules");
        model.addAttribute("schedules", scheduleService.findAll());
        return "schedule";
        } catch (Exception e) {
            log.error("Error fetching schedules", e);
            throw e;
        }
    }

    @PostMapping("/add")
    public String addSchedule(@RequestParam String title, 
                              @RequestParam String content,
                            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            log.debug("Adding new schedule: title={}, content={}, date={}", title, content, date);
            scheduleService.addSchedule(title, content, date);
        return "redirect:/schedule";
        } catch (Exception e) {
            log.error("Error adding schedule: title={}, content={}, date={}", title, content, date, e);
            throw e;
        }
    }

    @PostMapping("/delete")
    public String deleteSchedule(@RequestParam Long id) {
        try {
            log.debug("Deleting schedule with id={}", id);
        scheduleService.deleteSchedule(id);
        return "redirect:/schedule";
        } catch (Exception e) {
            log.error("Error deleting schedule with id={}", id, e);
            throw e;
        }
    }

    @GetMapping("/fragment")
    public String scheduleFragment(Model model) {
        model.addAttribute("schedules", scheduleService.findAll());
        return "schedule-fragment";
    }
} 
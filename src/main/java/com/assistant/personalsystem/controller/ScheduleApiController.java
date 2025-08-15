package com.assistant.personalsystem.controller;

import com.assistant.personalsystem.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleApiController {
    private final ScheduleService scheduleService;

    // 查询某月所有日程
    @GetMapping
    public ResponseEntity<List<?>> getSchedulesByMonthOrDate(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String date) {
        if (month != null) {
            // month: yyyy-MM
            String[] parts = month.split("-");
            int year = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return ResponseEntity.ok(scheduleService.findByMonth(year, m));
        } else if (date != null) {
            // date: yyyy-MM-dd
            LocalDate d = LocalDate.parse(date);
            return ResponseEntity.ok(scheduleService.findByDate(d));
        } else {
            return ResponseEntity.ok(scheduleService.findAll());
        }
    }

    // 新增日程
    @PostMapping
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleDto dto) {
        LocalDate d = LocalDate.parse(dto.getDate());
        scheduleService.addSchedule(dto.getTitle(), dto.getContent(), d);
        return ResponseEntity.ok().build();
    }

    // 删除日程
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok().build();
    }

    // DTO类
    public static class ScheduleDto {
        private String title;
        private String content;
        private String date; // yyyy-MM-dd
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }
} 
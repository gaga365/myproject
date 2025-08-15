package com.assistant.personalsystem.service;

import com.assistant.personalsystem.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<Schedule> findAll();
    void addSchedule(String title, String content, LocalDate date);
    void deleteSchedule(Long id);
    List<Schedule> findByMonth(int year, int month);
    List<Schedule> findByDate(LocalDate date);
} 
package com.assistant.personalsystem.repository;

import com.assistant.personalsystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByDate(LocalDate date);
    List<Schedule> findByDateBetween(LocalDate start, LocalDate end);
} 
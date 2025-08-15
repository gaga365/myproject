package com.assistant.personalsystem.service.impl;

import com.assistant.personalsystem.model.Schedule;
import com.assistant.personalsystem.repository.ScheduleRepository;
import com.assistant.personalsystem.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public void addSchedule(String title, String content, LocalDate date) {
        Schedule schedule = Schedule.builder()
                .title(title)
                .content(content)
                .date(date)
                .build();
        scheduleRepository.save(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    public List<Schedule> findByDate(LocalDate date) {
        return scheduleRepository.findByDate(date);
    }

    @Override
    public List<Schedule> findByMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return scheduleRepository.findByDateBetween(start, end);
    }
} 
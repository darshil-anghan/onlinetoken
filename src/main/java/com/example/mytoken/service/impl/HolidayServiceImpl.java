package com.example.mytoken.service.impl;

import com.example.mytoken.model.Holiday;
import com.example.mytoken.model.Queue;
import com.example.mytoken.payload.HolidayPayload;
import com.example.mytoken.repository.HolidayRepository;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final QueueRepository queueRepository;

    private void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot add a holiday in the past.");
        }
        if (date.isAfter(LocalDate.now().plusYears(1))) {
            throw new RuntimeException("Cannot add a holiday more than a year in advance.");
        }
    }

    @Override
    public void addHoliday(Long queueId, HolidayPayload payload) {
        LocalDate date = LocalDate.parse(payload.getDate());
        validateDate(date);
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        Holiday holiday = Holiday.builder()
                .description(payload.getDescription())
                .date(date)
                .queue(queue)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        holidayRepository.save(holiday);
    }

    @Override
    public void updateHoliday(Long holidayId, HolidayPayload payload) {
        Holiday holiday = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday not found"));

        LocalDate date = LocalDate.parse(payload.getDate());
        validateDate(date);

        holiday.setDescription(payload.getDescription());
        holiday.setDate(date);
        holiday.setUpdatedAt(LocalDateTime.now());

        holidayRepository.save(holiday);
    }

    @Override
    public void deleteHoliday(Long holidayId) {
        if (!holidayRepository.existsById(holidayId)) {
            throw new RuntimeException("Holiday not found");
        }
        holidayRepository.deleteById(holidayId);
    }

    @Override
    public List<HolidayPayload> getHolidaysByMonth(Long queueId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return holidayRepository.findByQueueIdAndDateBetween(queueId, start, end)
                .stream()
                .map(p -> new HolidayPayload(p.getId(), p.getDescription(), p.getDate().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public List<HolidayPayload> getHolidaysByYear(Long queueId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        return holidayRepository.findByQueueIdAndDateBetween(queueId, start, end)
                .stream()
                .map(p -> new HolidayPayload(p.getId(), p.getDescription(), p.getDate().toString()))
                .collect(Collectors.toList());
    }
}

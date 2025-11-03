package com.example.mytoken.service.impl;

import com.example.mytoken.model.Queue;
import com.example.mytoken.model.QueueSession;
import com.example.mytoken.model.response.AvailableSlotsResponse;
import com.example.mytoken.repository.HolidayRepository;
import com.example.mytoken.repository.PatientQueueRepository;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.repository.QueueSessionRepository;
import com.example.mytoken.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final QueueRepository queueRepository;
    private final HolidayRepository holidayRepository;
    private final QueueSessionRepository sessionRepository;
    private final PatientQueueRepository patientQueueRepository;

    private static final List<String> SESSION_ORDER = List.of("Morning", "Afternoon", "Evening", "Night", "Late Night");

    @Override
    public AvailableSlotsResponse getAvailableSlots(Long queueId, String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime now = LocalDateTime.now();

        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        if (holidayRepository.existsByQueueIdAndDate(queueId, date)) {
            return new AvailableSlotsResponse(Collections.emptyList());
        }

        if (date.isBefore(now.toLocalDate())) {
            return new AvailableSlotsResponse(Collections.emptyList());
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<QueueSession> sessions = sessionRepository.findByQueueIdAndDayOfWeek(queueId, dayOfWeek);

        if (sessions.isEmpty()) {
            return new AvailableSlotsResponse(Collections.emptyList());
        }

        sessions.sort(Comparator.comparingInt(s ->
                SESSION_ORDER.indexOf(Optional.ofNullable(s.getSessionName()).orElse("").trim())));

        ZoneId zoneId = ZoneId.of("Asia/Kolkata");

        List<AvailableSlotsResponse.SlotAvailability> slotList = new ArrayList<>();
        for (QueueSession session : sessions) {
            LocalDateTime slotTime = LocalDateTime.of(date, session.getStartTime());
            LocalDateTime endTime = LocalDateTime.of(date, session.getEndTime());

            while (slotTime.isBefore(endTime)) {
                if (date.isAfter(now.toLocalDate()) || slotTime.isAfter(now)) {
                    long epochMillis = slotTime.atZone(zoneId).toInstant().toEpochMilli();

                    boolean isBooked = patientQueueRepository.existsByQueueIdAndAppointmentBookingDateAndOnlineBookingSlot(
                            queueId,
                            dateStr,
                            epochMillis
                    );

                    slotList.add(new AvailableSlotsResponse.SlotAvailability(
                            epochMillis,
                            !isBooked,
                            session.getSessionName()
                    ));
                }

                slotTime = slotTime.plusMinutes(queue.getAvgServiceDuration());
            }
        }

        return new AvailableSlotsResponse(slotList);
    }
}

package com.example.mytoken.service.impl;

import com.example.mytoken.model.Queue;
import com.example.mytoken.model.QueueSession;
import com.example.mytoken.payload.QueueSessionPayload;
import com.example.mytoken.repository.QueueRepository;
import com.example.mytoken.repository.QueueSessionRepository;
import com.example.mytoken.service.QueueSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueSessionServiceImpl implements QueueSessionService {

    private final QueueRepository queueRepository;
    private final QueueSessionRepository sessionRepository;

    @Transactional
    @Override
    public void createSessions(Long queueId, List<QueueSessionPayload> payloads) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found with id: " + queueId));

        // Fetch existing sessions by queueId
        List<QueueSession> existingSessions = sessionRepository.findByQueueId(queueId);

        // Create a map for quick lookup: sessionName (lowercased & trimmed) => QueueSession
        var existingSessionMap = existingSessions.stream().collect(Collectors.toMap(
                s -> s.getSessionName().toLowerCase().trim(),
                s -> s
        ));

        List<QueueSession> updatedSessions = new ArrayList<>();

        for (QueueSessionPayload payload : payloads) {
            String sessionNameKey = payload.getSessionName().toLowerCase().trim();
            LocalTime startTime = LocalTime.parse(payload.getStartTime());
            LocalTime endTime = LocalTime.parse(payload.getEndTime());

            if (existingSessionMap.containsKey(sessionNameKey)) {
                // Update existing session
                QueueSession existing = existingSessionMap.get(sessionNameKey);
                existing.setStartTime(startTime);
                existing.setEndTime(endTime);
                existing.setDayOfWeek(payload.getDayOfWeek());
                existing.setUpdatedAt(LocalDateTime.now());
                updatedSessions.add(existing);
            } else {
                // Create new session
                QueueSession newSession = QueueSession.builder()
                        .queue(queue)
                        .sessionName(payload.getSessionName())
                        .startTime(startTime)
                        .endTime(endTime)
                        .dayOfWeek(payload.getDayOfWeek())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                updatedSessions.add(newSession);
            }
        }

        // Save all updated and new sessions
        sessionRepository.saveAll(updatedSessions);
    }

    @Transactional
    @Override
    public void updateSession(Long sessionId, QueueSessionPayload payload) {
        QueueSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));

        session.setSessionName(payload.getSessionName());
        session.setStartTime(LocalTime.parse(payload.getStartTime()));
        session.setEndTime(LocalTime.parse(payload.getEndTime()));
        session.setUpdatedAt(LocalDateTime.now());

        sessionRepository.save(session);
    }

    @Transactional
    @Override
    public void deleteSession(Long sessionId) {
        QueueSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        sessionRepository.delete(session);
    }

    @Override
    public List<QueueSessionPayload> getSessionsByQueueId(Long queueId) {
        List<QueueSession> sessions = sessionRepository.findByQueueId(queueId);

        return sessions.stream().map(s -> QueueSessionPayload.builder()
                .sessionName(s.getSessionName())
                .startTime(s.getStartTime().toString())
                .endTime(s.getEndTime().toString())
                .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                .updatedAt(s.getUpdatedAt() != null ? s.getUpdatedAt().toString() : null)
                .build())
                .collect(Collectors.toList());
    }
}

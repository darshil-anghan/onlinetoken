package com.example.mytoken.service;

import com.example.mytoken.payload.QueueSessionPayload;

import java.util.List;

public interface QueueSessionService {
    void createSessions(Long queueId, List<QueueSessionPayload> payloads);
    void updateSession(Long sessionId, QueueSessionPayload payload);
    void deleteSession(Long sessionId);
    List<QueueSessionPayload> getSessionsByQueueId(Long queueId);
}

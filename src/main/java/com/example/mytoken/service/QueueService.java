package com.example.mytoken.service;

import com.example.mytoken.model.Queue;
import com.example.mytoken.payload.QueuePayload;

import java.util.List;

public interface QueueService {
    void saveQueue(Long adminId, QueuePayload payload);

    void updateQueue(Long adminId, QueuePayload payload);

    void assignQueueToUser(Long userId, Long queueId, Long adminId);

    List<Queue> getAdminAllQueue(Long adminId);

    List<Queue> getUserAllQueue(Long userId);

    void updateQueueActiveStatus(Long queueId, boolean isActive);

    void updateQueueActiveCounter(Long queueId, int noOfCounter);

    String generateToken(Long queueId);

    Queue getQueueByToken(String token);
}

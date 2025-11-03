package com.example.mytoken.service;

import com.example.mytoken.model.response.AvailableSlotsResponse;

public interface SlotService {
    AvailableSlotsResponse getAvailableSlots(Long queueId, String date);
}

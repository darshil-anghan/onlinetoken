package com.example.mytoken.controller;

import com.example.mytoken.model.response.AvailableSlotsResponse;
import com.example.mytoken.model.response.GlobalResponse;
import com.example.mytoken.payload.AvailableSlotsPayload;
import com.example.mytoken.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class SlotController extends BaseController {

    private final SlotService slotService;

    /**
     *  This function use for fetch available slots in appointment section
     */
    @PostMapping("/available")
    public ResponseEntity<GlobalResponse> getAvailableSlots(@RequestBody AvailableSlotsPayload payload) {
        try {
            AvailableSlotsResponse response = slotService.getAvailableSlots(payload.getQueueId(), payload.getDate());
            return ok(response, "Available slots fetched successfully");
        } catch (Exception e) {
            return ok(null, "Error: " + e.getMessage());
        }
    }
}

package com.example.mytoken.model.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSlotsResponse {
    private List<SlotAvailability> availableSlots;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SlotAvailability {
        private Long slot;
        private boolean available;
        private String sessionName;
    }
}

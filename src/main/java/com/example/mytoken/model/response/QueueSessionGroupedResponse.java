package com.example.mytoken.model.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueSessionGroupedResponse {
    private String dayOfWeek;
    private List<TimeSlot> timeSlot;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TimeSlot {
        private String sessionName;
        private String startTime;
        private String endTime;
    }
}

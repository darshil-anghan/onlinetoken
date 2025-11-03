package com.example.mytoken.payload;

import lombok.*;

import java.time.DayOfWeek;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueueSessionPayload {
    private String sessionName;
    private DayOfWeek dayOfWeek;
    private String startTime;
    private String endTime;
    private String updatedAt;
    private String createdAt;
}

package com.example.mytoken.payload;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSlotsPayload {
    private Long queueId;
    private String date;
}

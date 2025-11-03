package com.example.mytoken.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HolidayPayload {
    private Long id;
    private String description;
    private String date;
}

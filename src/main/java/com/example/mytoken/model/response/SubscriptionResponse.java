package com.example.mytoken.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SubscriptionResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private boolean isActive = true;
    private Integer maxUsers;
    private Integer maxQueues;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

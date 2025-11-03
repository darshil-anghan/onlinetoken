package com.example.mytoken.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPayload {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;

    @NotBlank(message = "Price cannot be blank")
    private Double price;

    @NotBlank(message = "Duration cannot be blank")
    private Integer duration;

    private boolean isActive = true;

    @NotBlank(message = "Max Users cannot be blank")
    private Integer maxUsers;

    @NotBlank(message = "Max Queues cannot be blank")
    private Integer maxQueues;
}

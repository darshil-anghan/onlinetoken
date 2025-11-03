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
public class UserSubscriptionPayload {

    @NotBlank(message = "User ID cannot be blank")
    private Long userId;

    @NotBlank(message = "Subscription ID cannot be blank")
    private Long subscriptionId;

    @NotBlank(message = "Start date cannot be blank")
    private Long amount;

    private boolean isActive = true;

    private String status = "active";
}

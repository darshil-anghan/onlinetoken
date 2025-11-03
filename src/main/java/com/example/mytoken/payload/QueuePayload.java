package com.example.mytoken.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueuePayload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Queue name is required")
    private String name;

    @NotBlank(message = "Queue prefix is required")
    private String prefix;

    @NotBlank(message = "Queue description is required")
    private String description;

    @NotBlank(message = "Queue no of consumer is required")
    private int noOfCustomer;

    @NotBlank(message = "Queue average service duration is required")
    private int avgServiceDuration;

    @NotBlank(message = "Service center default 1 enter.")
    private int serviceCenter;

    private boolean isActive;
}

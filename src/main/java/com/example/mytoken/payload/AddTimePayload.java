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
public class AddTimePayload {

    @NotBlank(message = "Queue Id not be blank")
    private Long queueId;

    @NotBlank(message = "Token Id not be blank")
    private Long tokenId;

    @NotBlank(message = "Service Time not be blank")
    private Long serviceTime;
}
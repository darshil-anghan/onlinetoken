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
public class PatientQueueOtpPayload {

    @NotBlank(message = "Otp is can't be blank.")
    private Long otpId;

    @NotBlank(message = "Otp can't be blank")
    private String otp;

    @NotBlank(message = "Patient number not found.")
    private Long patientId;
}

package com.example.mytoken.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpVerificationPayload {

    private Long userId;
    private Long otpId;
    private String otp;
}

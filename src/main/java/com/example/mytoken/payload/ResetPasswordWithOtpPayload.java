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
public class ResetPasswordWithOtpPayload {

    private Long userId;
    private Long otpId;
    private String otp;

    @NotBlank
    private String newPassword;
}

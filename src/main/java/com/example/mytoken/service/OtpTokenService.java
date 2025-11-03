package com.example.mytoken.service;

public interface OtpTokenService {
    void createAndSendOtp(Long userId, String email);
    boolean verifyOtp(Long userId, Long otpId, String otp);
}

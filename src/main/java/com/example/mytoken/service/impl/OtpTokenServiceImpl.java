package com.example.mytoken.service.impl;

import com.example.mytoken.model.OtpToken;
import com.example.mytoken.repository.OtpTokenRepository;
import com.example.mytoken.service.EmailService;
import com.example.mytoken.service.OtpTokenService;
import com.example.mytoken.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpTokenServiceImpl implements OtpTokenService {

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void createAndSendOtp(Long userId, String email) {
        String otp = Utility.generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        Optional<OtpToken> existing = otpTokenRepository.findByUserId(userId);
        OtpToken token = existing.orElse(new OtpToken());

        token.setUserId(userId);
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiryTime(expiryTime);

        otpTokenRepository.save(token);

        // Send OTP via email
        emailService.sendOtpEmail(email, otp, token.getId(), userId);
    }

    @Override
    public boolean verifyOtp(Long userId, Long otpId, String otp) {
        Optional<OtpToken> tokenOpt = otpTokenRepository.findById(otpId);

        if (tokenOpt.isPresent()) {
            OtpToken token = tokenOpt.get();

            return token.getUserId().equals(userId) &&
                    token.getOtp().equals(otp) &&
                    token.getExpiryTime().isAfter(LocalDateTime.now());
        }

        return false;
    }
}


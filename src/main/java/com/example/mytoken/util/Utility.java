package com.example.mytoken.util;

import com.example.mytoken.service.JwtService;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
public class Utility {

    public static String generateOtp() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getEmailFromToken(String token, JwtService jwtService) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtService.extractUsername(token); // ✅ extract subject (email)
    }

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOtp1() {
        int otp = 100000 + secureRandom.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    public static String generateQueueLink() {
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            // Generate a 3-character random chunk
            for (int j = 0; j < 3; j++) {
                int index = secureRandom.nextInt(GlobalConstant.ALPHANUM.length());
                token.append(GlobalConstant.ALPHANUM.charAt(index));
            }

            if (i < 2) {
                token.append('-');
            }
        }
        return token.toString();
    }

}
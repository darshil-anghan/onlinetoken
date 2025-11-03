package com.example.mytoken.service.impl;

import com.example.mytoken.exception.InvalidRequestException;
import com.example.mytoken.model.AuthToken;
import com.example.mytoken.model.UserInfo;
import com.example.mytoken.payload.LoginPayload;
import com.example.mytoken.repository.UserInfoRepository;
import com.example.mytoken.service.AuthService;
import com.example.mytoken.service.JwtService;
import com.example.mytoken.service.OtpTokenService;
import com.example.mytoken.util.GlobalConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserInfoRepository userRepository;
    private final OtpTokenService otpTokenService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserInfoRepository userRepository,
                           OtpTokenService otpTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.otpTokenService = otpTokenService;
    }

    @Override
    public AuthToken authenticate(LoginPayload loginPayload) {
        UserInfo authenticatedUser = userRepository.findByEmail(loginPayload.getEmail())
                .orElseThrow(() -> new NoSuchElementException(GlobalConstant.USER_NOT_FOUND_MSG));

        if(!authenticatedUser.isActive()) {
            otpTokenService.createAndSendOtp(authenticatedUser.getId(), authenticatedUser.getEmail());
            throw new InvalidRequestException("Email sent to your registered mail please verify first.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginPayload.getEmail(), loginPayload.getPassword()));
        } catch (Exception ex) {
            throw new InvalidRequestException("Invalid Username and password");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authenticatedUser.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER");
        claims.put("isAdmin", authenticatedUser.isAdmin());
        claims.put("sub", authenticatedUser.getEmail());
        claims.put("authorities", Collections.singletonList(authenticatedUser.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER"));

        String jwtToken = jwtService.generateToken(claims, authenticatedUser);
        return AuthToken.builder()
                .token(jwtToken)
                .build();
    }
}

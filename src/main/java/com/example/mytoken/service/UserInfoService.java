package com.example.mytoken.service;

import com.example.mytoken.model.UserInfo;
import com.example.mytoken.payload.ResetPasswordWithOtpPayload;
import com.example.mytoken.payload.UserPayload;

public interface UserInfoService {

    UserInfo signup(UserPayload user);

    void resetPassword(String email, String oldPassword, String newPassword);

    void markUserVerified(Long userId);

    void forgotPassword(String mail);

    void resetPasswordWithOtp(ResetPasswordWithOtpPayload request);

    UserInfo getById(Long id);

    UserInfo getUserInfoByEmail(String email);

    UserInfo updateUserInfoByEmail(UserPayload userPayload);

    String addUser(Long adminId, UserPayload user);
}

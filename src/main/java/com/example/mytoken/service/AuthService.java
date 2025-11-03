package com.example.mytoken.service;

import com.example.mytoken.model.AuthToken;
import com.example.mytoken.payload.LoginPayload;

public interface AuthService {
    AuthToken authenticate(LoginPayload loginPayload);
}

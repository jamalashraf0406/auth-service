package com.example.authservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoginParam {
    AUTHORIZATION("Authorization"),
    REFRESH_TOKEN_URI("/auth/token/refresh"),
    REFRESH_TOKEN_HEADER("X-Grant-Type"),
    REFRESH_TOKEN_VALUE("refresh_token"),
    LOGIN_URI("/auth/login");

    private final String param;
}

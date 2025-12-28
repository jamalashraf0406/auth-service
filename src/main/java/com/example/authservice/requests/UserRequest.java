package com.example.authservice.requests;

import com.example.authservice.enums.RoleName;

public record UserRequest(String username, String password, boolean enabled, RoleName name) {
}

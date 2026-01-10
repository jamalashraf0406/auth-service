package com.bluesoft.authservice.requests;

import com.bluesoft.authservice.enums.RoleName;

public record UserRequest(String username, String password, boolean enabled, RoleName name) {
}

package com.example.authservice.requests;


import lombok.NonNull;

public record LoginRequest(String username, String password) {

    @Override
    @NonNull
    public String toString() {
        return username() + ":" + password();
    }
}

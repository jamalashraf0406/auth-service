package com.example.authservice.controllers;

import com.example.authservice.requests.UserCreateRequest;
import com.example.authservice.resources.UserResourceV1;
import com.example.authservice.responses.UserCreateResponse;
import com.example.authservice.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserControllerV1 implements UserResourceV1 {

    private final UserService userService;

    @Override
    public ResponseEntity<UserCreateResponse> createUser(UserCreateRequest request) {
        this.userService.createUser(User.builder()
                .username(request.username())
                .password(request.password())
                .roles("USER").build());
        URI uri = URI.create("/user/v1/create");
        return ResponseEntity.created(uri).body(
                new UserCreateResponse("User create successfully!"));
    }
}

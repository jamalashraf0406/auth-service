package com.example.authservice.resources;

import com.example.authservice.requests.UserCreateRequest;
import com.example.authservice.responses.UserCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/user/v1")
public interface UserResourceV1 {

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<UserCreateResponse> createUser(@RequestBody UserCreateRequest request);
}

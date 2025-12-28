package com.example.authservice.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class InitializeData {

    @Bean
    public CommandLineRunner initData(UserDetailsManager userDetailsManager) {
        return args -> {
            // 1. Create a Test User
            if (!userDetailsManager.userExists("jashraf07")) {
                UserDetails user = User.builder()
                        .username("jashraf07")
                        .password("password123")
                        .roles("ADMIN")
                        .build();
                userDetailsManager.createUser(user);
            }
        };
    }
}

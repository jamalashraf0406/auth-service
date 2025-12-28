package com.example.authservice.configs;

import com.example.authservice.enums.LoginParam;
import com.example.authservice.filters.JsonUsernamePasswordAuthFilter;
import com.example.authservice.filters.JwtAuthenticationOncePerRequestFilter;
import com.example.authservice.filters.RefreshTokenAuthenticationOncePerRequestFilter;
import com.example.authservice.filters.RefreshTokenAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class AuthenticationConfig {

    @Bean
    public SecurityFilterChain security(
            HttpSecurity http, JsonUsernamePasswordAuthFilter loginFilter,
            RefreshTokenAuthenticationOncePerRequestFilter refreshFilter,
            JwtAuthenticationOncePerRequestFilter jwtAuthenticationFilter ) throws Exception {

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/.well-known/jwks.json")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**","/.well-known/jwks.json").permitAll()
                        .requestMatchers(HttpMethod.GET, LoginParam.REFRESH_TOKEN_URI.getParam()).permitAll()
                        .requestMatchers(HttpMethod.POST, LoginParam.LOGIN_URI.getParam()).permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/v1/create").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // Disable frame options for H2
                .headers(headers -> headers
                        //.frameOptions(frame -> frame.disable())
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .addFilterBefore(refreshFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /* Don't use below AuthenticatorManager as it uses default AuthenticatorProvider not the
       One you have implemented.
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }*/

    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoProvider,
            RefreshTokenAuthenticationProvider refreshProvider) {

        return new ProviderManager(
                List.of(daoProvider, refreshProvider)
        );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


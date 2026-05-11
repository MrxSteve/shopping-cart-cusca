package com.prueba.auth_service.config;

import com.prueba.auth_service.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public ApplicationRunner initJwt() {
        return args -> {
            log.info("Initializing JWT Token Provider");
            jwtTokenProvider.init();
            log.info("JWT Token Provider initialized successfully");
        };
    }
}

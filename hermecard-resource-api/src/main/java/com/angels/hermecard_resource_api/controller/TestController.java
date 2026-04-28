package com.angels.hermecard_resource_api.controller;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

// TestController.java
@RestController
public class TestController {

    @GetMapping("/public/hello")
    public Mono<String> publico() {
        return Mono.just("sem token, funcionou!");
    }

    @GetMapping("/privado/hello")
    public Mono<String> privado(JwtAuthenticationToken token) {
        return Mono.just("olá, " + token.getName());
    }

    @GetMapping("/privado/claims")
    public Mono<Map<String, Object>> claims(JwtAuthenticationToken token) {
        return Mono.just(token.getTokenAttributes());
    }
}
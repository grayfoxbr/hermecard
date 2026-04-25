package com.angels.hermecard_resource_api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthInfoController {

    /**
     * GET /api/me
     * Retorna dados do usuário autenticado extraídos do JWT.
     * Exige scope "openid".
     */
    @GetMapping("/me")
    public Mono<Map<String, Object>> me(@AuthenticationPrincipal Jwt jwt) {
        return Mono.just(Map.of(
                "username",  jwt.getSubject(),
                "issuer",    jwt.getIssuer().toString(),
                "issuedAt",  jwt.getIssuedAt().toString(),
                "expiresAt", jwt.getExpiresAt().toString(),
                "scopes",    jwt.getClaimAsStringList("scope")
        ));
    }

    /**
     * GET /api/token-info
     * Retorna TODOS os claims do JWT — útil para debug.
     * Exige apenas token válido.
     */
    @GetMapping("/token-info")
    public Mono<Map<String, Object>> tokenInfo(@AuthenticationPrincipal Jwt jwt) {
        return Mono.just(jwt.getClaims());
    }
}
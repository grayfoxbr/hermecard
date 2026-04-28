package com.angels.hermecard_auth_api.config;

import com.angels.hermecard_auth_api.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CustomTokenCustomizer {

    private final UserRepository userRepository;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (context.getTokenType().getValue().equals("access_token")) {
                String username = context.getPrincipal().getName();
                userRepository.findByUsername(username).ifPresent(user -> {
                    String role = user.getRole().replace("ROLE_", "");
                    context.getClaims().claim("roles", List.of(role));
                });
            }
        };
    }
}
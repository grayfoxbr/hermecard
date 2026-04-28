package com.angels.hermecard_resource_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity // ✅ habilita @PreAuthorize nos handlers reativos
public class ResourceServerConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(auth -> auth
                        // Leitura: exige scope "read"
                        .pathMatchers(HttpMethod.GET,    "/api/cards/**").hasAuthority("SCOPE_read")

                        // Escrita: exige scope "write"
                        .pathMatchers(HttpMethod.POST,   "/api/cards/**").hasAuthority("SCOPE_write")
                        .pathMatchers(HttpMethod.PUT,    "/api/cards/**").hasAuthority("SCOPE_write")
                        .pathMatchers(HttpMethod.DELETE, "/api/cards/**").hasAuthority("SCOPE_write")

                        // Info do usuário: exige scope "openid"
                        .pathMatchers("/api/me").hasAuthority("SCOPE_openid")

                        // Claims do token: qualquer token válido
                        .pathMatchers("/api/token-info").authenticated()

                        // Admin: exige ROLE_ADMIN
                        .pathMatchers("/api/admin/**").hasRole("ADMIN")

                        .anyExchange().authenticated()
                )

                // ✅ Resource Server reativo — busca JWKs do Authorization Server automaticamente
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    /**
     * Converte claims do JWT em authorities Spring Security (versão reativa).
     *
     * scope "read"   → SCOPE_read
     * scope "write"  → SCOPE_write
     * roles "ADMIN"  → ROLE_ADMIN
     */
    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        // Converter de scopes
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        scopesConverter.setAuthoritiesClaimName("scope");
        scopesConverter.setAuthorityPrefix("SCOPE_");

        // Converter de roles
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("ROLE_");

        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                reactor.core.publisher.Flux.fromIterable(
                        java.util.stream.Stream.concat(
                                scopesConverter.convert(jwt).stream(),
                                rolesConverter.convert(jwt).stream()
                        ).toList()
                )
        );

        return converter;
    }
}
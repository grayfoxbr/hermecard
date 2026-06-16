package com.angels.hermecard_auth_api_server.config;

import com.angels.hermecard_auth_api_server.model.User;
import com.angels.hermecard_auth_api_server.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // =========================================================
        // CLIENT OAUTH2
        // =========================================================
        if (registeredClientRepository.findByClientId("meu-client") == null) {

            RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("meu-client")
                    .clientName("Meu Client")
                    .clientIdIssuedAt(Instant.now())

                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)

                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                    .redirectUri("http://localhost:8081/login/oauth2/code/meu-client")
                    .redirectUri("http://localhost:8081/callback")
                    .redirectUri("com.example.appauthbase:/oauth2redirect")
                    .postLogoutRedirectUri("http://localhost:8081/logout")

                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("read")
                    .scope("write")

                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(false)
                            .requireProofKey(true)
                            .build())

                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(1))
                            .refreshTokenTimeToLive(Duration.ofDays(7))
                            .reuseRefreshTokens(false)
                            .build())

                    .build();

            registeredClientRepository.save(client);
        }

        // =========================================================
        // USUÁRIO
        // =========================================================
        if (!userRepository.existsByUsername("admin")) {

            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .role("ROLE_ADMIN")

                    .firstName("Raul")
                    .lastName("Angels")
                    .email("admin@hermecard.com")

                    .emailVerified(true)

                    .enabled(true)
                    .accountLocked(false)

                    .phoneNumber("+5511999999999")
                    .phoneVerified(true)

                    .locale("pt-BR")
                    .zoneinfo("America/Sao_Paulo")

                    .pictureUrl("https://i.pravatar.cc/300")
                    .build();

            userRepository.save(user);
        }
    }
}

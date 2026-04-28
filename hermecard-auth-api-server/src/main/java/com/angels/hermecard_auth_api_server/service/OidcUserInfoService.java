package com.angels.hermecard_auth_api_server.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.angels.hermecard_auth_api_server.model.User;
import com.angels.hermecard_auth_api_server.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

/**
 * Example service to perform lookup of user info for customizing an {@code id_token}.
 */
@Service
@RequiredArgsConstructor
public class OidcUserInfoService {

    private final UserRepository userRepository;

    public OidcUserInfo loadUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return OidcUserInfo.builder()
                .subject(user.getUsername())
                .preferredUsername(user.getUsername())
                .name(user.getFirstName() + " " + user.getLastName())
                .givenName(user.getFirstName())
                .familyName(user.getLastName())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .phoneNumber(user.getPhoneNumber())
                .phoneNumberVerified(user.isPhoneVerified())
                .picture(user.getPictureUrl())
                .locale(user.getLocale())
                .zoneinfo(user.getZoneinfo())
                .updatedAt(user.getUpdatedAt().toString())
                .claim("role", user.getRole())
                .build();
    }
}

package com.angels.hermecard_auth_api_server.service;

import com.angels.hermecard_auth_api_server.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRole())
                        .accountLocked(user.isAccountLocked())
                        .disabled(!user.isEnabled())
                        .build()
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado: " + email));
    }
}
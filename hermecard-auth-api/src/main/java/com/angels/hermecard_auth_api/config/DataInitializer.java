package com.angels.hermecard_auth_api.config;

import com.angels.hermecard_auth_api.model.User;
import com.angels.hermecard_auth_api.repo.UserRepository;
import com.zaxxer.hikari.util.Credentials;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepo.existsByUsername("admin@email.com")) {

            User user = User.builder()
                    .username("admin@email.com")
                    .password(passwordEncoder.encode("123456"))
                    .role("ROLE_ADMIN")
                    .build();

            userRepo.save(user);

            user.setUsername("admin@email.com");

            userRepo.save(user);
        }
    }
}

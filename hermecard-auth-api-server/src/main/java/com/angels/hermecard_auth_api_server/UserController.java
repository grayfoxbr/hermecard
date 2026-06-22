package com.angels.hermecard_auth_api_server;

import com.angels.hermecard_auth_api_server.model.User;
import com.angels.hermecard_auth_api_server.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cadastra um novo usuário.
     *
     * Body esperado:
     * {
     *   "email": "usuario@exemplo.com",
     *   "password": "senha123"
     * }
     *
     * Response 201:
     * {
     *   "id": 1,
     *   "email": "usuario@exemplo.com"
     * }
     */
    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (request.email() == null || request.email().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "E-mail é obrigatório"));
        }

        if (request.password() == null || request.password().length() < 6) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Senha deve ter pelo menos 6 caracteres"));
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "E-mail já cadastrado"));
        }

        // Extrai a parte local do e-mail como username (ex: "fulano" de "fulano@email.com")
        String username = request.email().split("@")[0];

        // Garante username único adicionando sufixo numérico se necessário
        String finalUsername = username;
        int suffix = 1;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = username + suffix++;
        }

        User user = User.builder()
                .username(finalUsername)
                .password(passwordEncoder.encode(request.password()))
                .role("ROLE_USER")
                .firstName(finalUsername)
                .lastName("")
                .email(request.email())
                .emailVerified(false)
                .enabled(true)
                .accountLocked(false)
                .build();

        User saved = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterResponse(saved.getId(), saved.getEmail()));
    }

    // ── DTOs ────────────────────────────────────────────────────────────────

    public record RegisterRequest(String email, String password) {}

    public record RegisterResponse(Long id, String email) {}
}
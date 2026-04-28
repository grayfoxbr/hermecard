package com.angels.hermecard_auth_api.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        // 👇 ignora favicon
        if (request.getRequestURI().equals("/favicon.ico")) {
            return ResponseEntity.notFound().build();
        }

        log.warn("⚠️ RESOURCE NOT FOUND [{} {}]",
                request.getMethod(),
                request.getRequestURI());

        return ResponseEntity.status(404).body(Map.of(
                "status", 404,
                "error", "not_found",
                "path", request.getRequestURI()
        ));
    }

    // 🔥 Qualquer exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("💥 INTERNAL ERROR [{} {}]: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 500,
                "error", "internal_server_error",
                "message", ex.getMessage(),
                "path", request.getRequestURI()
        ));
    }

    // 🔐 Erro de autenticação
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("🔐 AUTH ERROR [{} {}]: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 401,
                "error", "unauthorized",
                "message", ex.getMessage(),
                "path", request.getRequestURI()
        ));
    }

    // 🔒 Acesso negado
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("⛔ ACCESS DENIED [{} {}]: {}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 403,
                "error", "forbidden",
                "message", ex.getMessage(),
                "path", request.getRequestURI()
        ));
    }
}

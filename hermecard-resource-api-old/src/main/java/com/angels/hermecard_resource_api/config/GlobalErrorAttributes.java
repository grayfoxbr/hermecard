package com.angels.hermecard_resource_api.config;

import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, org.springframework.boot.web.error.ErrorAttributeOptions options) {
        Throwable error = getError(request);

        // Erros de validação (@Valid)
        if (error instanceof WebExchangeBindException ex) {
            var fields = ex.getBindingResult().getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            f -> f.getField(),
                            f -> f.getDefaultMessage()
                    ));
            return Map.of(
                    "error", "validation_failed",
                    "fields", fields,
                    "status", HttpStatus.BAD_REQUEST.value()
            );
        }

        // Erros de negócio (NOT_FOUND, FORBIDDEN, etc.)
        if (error instanceof ResponseStatusException ex) {
            return Map.of(
                    "error", ex.getReason() != null ? ex.getReason() : "error",
                    "status", ex.getStatusCode().value()
            );
        }

        // Genérico
        return Map.of(
                "error", "internal_server_error",
                "message", error.getMessage() != null ? error.getMessage() : "Unknown error",
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
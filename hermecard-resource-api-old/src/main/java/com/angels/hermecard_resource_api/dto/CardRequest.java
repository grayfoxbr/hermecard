package com.angels.hermecard_resource_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CardRequest(

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Bandeira é obrigatória")
        String brand,

        @NotNull
        @Positive(message = "Limite deve ser positivo")
        BigDecimal limit,

        @NotNull
        BigDecimal currentBalance,

        @NotNull
        Integer closingDay,

        @NotNull
        Integer dueDay
) {}

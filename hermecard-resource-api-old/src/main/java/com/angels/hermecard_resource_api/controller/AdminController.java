package com.angels.hermecard_resource_api.controller;

import com.angels.hermecard_resource_api.dto.CardResponse;
import com.angels.hermecard_resource_api.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CardService cardService;

    /**
     * GET /api/admin/cards
     * Lista TODOS os cartões de todos os usuários.
     * Exige ROLE_ADMIN no token (injetado pelo CustomTokenCustomizer do Auth Server).
     */
    @GetMapping("/cards")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<CardResponse> listAllCards() {
        return cardService.listAllCards();
    }
}

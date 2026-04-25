package com.angels.hermecard_resource_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // GET /api/cards — lista cartões do usuário autenticado
    @GetMapping
    public Flux<CardResponse> listMyCards(@AuthenticationPrincipal Jwt jwt) {
        return cardService.listMyCards(jwt.getSubject());
    }

    // GET /api/cards/{id}
    @GetMapping("/{id}")
    public Mono<CardResponse> getCard(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return cardService.getCard(id, jwt.getSubject());
    }

    // POST /api/cards — cria novo cartão
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CardResponse> createCard(
            @Valid @RequestBody CardRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return cardService.createCard(request, jwt.getSubject());
    }

    // PUT /api/cards/{id} — atualiza cartão
    @PutMapping("/{id}")
    public Mono<CardResponse> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return cardService.updateCard(id, request, jwt.getSubject());
    }

    // DELETE /api/cards/{id} — deleta cartão
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCard(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return cardService.deleteCard(id, jwt.getSubject());
    }
}
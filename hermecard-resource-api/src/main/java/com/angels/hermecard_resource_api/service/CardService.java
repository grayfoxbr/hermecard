package com.angels.hermecard_resource_api.service;

import com.angels.hermecard_resource_api.dto.CardRequest;
import com.angels.hermecard_resource_api.dto.CardResponse;
import com.angels.hermecard_resource_api.model.Card;
import com.angels.hermecard_resource_api.repo.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    // Lista cartões do usuário autenticado
    public Flux<CardResponse> listMyCards(String username) {
        return cardRepository.findByOwnerUsername(username)
                .map(CardResponse::from);
    }

    // Busca cartão por ID (verifica dono)
    public Mono<CardResponse> getCard(Long id, String username) {
        return cardRepository.findByIdAndOwnerUsername(id, username)
                .map(CardResponse::from)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cartão não encontrado"
                )));
    }

    // Cria novo cartão vinculado ao usuário autenticado
    public Mono<CardResponse> createCard(CardRequest request, String username) {
        Card card = Card.builder()
                .ownerUsername(username)
                .name(request.name())
                .brand(request.brand())
                .limit(request.limit())
                .currentBalance(request.currentBalance())
                .closingDay(request.closingDay())
                .dueDay(request.dueDay())
                .build();

        return cardRepository.save(card)
                .map(CardResponse::from);
    }

    // Atualiza cartão (verifica dono)
    public Mono<CardResponse> updateCard(Long id, CardRequest request, String username) {
        return cardRepository.findByIdAndOwnerUsername(id, username)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cartão não encontrado"
                )))
                .flatMap(card -> {
                    card.setName(request.name());
                    card.setBrand(request.brand());
                    card.setLimit(request.limit());
                    card.setCurrentBalance(request.currentBalance());
                    card.setClosingDay(request.closingDay());
                    card.setDueDay(request.dueDay());
                    return cardRepository.save(card);
                })
                .map(CardResponse::from);
    }

    // Deleta cartão (verifica dono)
    public Mono<Void> deleteCard(Long id, String username) {
        return cardRepository.findByIdAndOwnerUsername(id, username)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cartão não encontrado"
                )))
                .flatMap(cardRepository::delete);
    }

    // Lista TODOS os cartões — admin only
    public Flux<CardResponse> listAllCards() {
        return cardRepository.findAll()
                .map(CardResponse::from);
    }
}
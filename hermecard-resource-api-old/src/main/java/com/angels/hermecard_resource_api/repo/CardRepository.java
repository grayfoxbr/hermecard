package com.angels.hermecard_resource_api.repo;

import com.angels.hermecard_resource_api.model.Card;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CardRepository extends ReactiveCrudRepository<Card, Long> {

    // Todos os cartões do usuário autenticado
    Flux<Card> findByOwnerUsername(String ownerUsername);

    // Cartão por ID garantindo que pertence ao usuário
    Mono<Card> findByIdAndOwnerUsername(Long id, String ownerUsername);
}

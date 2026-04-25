package com.angels.hermecard_resource_api.dto;

import com.angels.hermecard_resource_api.model.Card;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CardResponse(
        Long id,
        String ownerUsername,
        String name,
        String brand,
        BigDecimal limit,
        BigDecimal currentBalance,
        Integer closingDay,
        Integer dueDay,
        Boolean active,
        LocalDateTime createdAt
) {
    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getOwnerUsername(),
                card.getName(),
                card.getBrand(),
                card.getLimit(),
                card.getCurrentBalance(),
                card.getClosingDay(),
                card.getDueDay(),
                card.getActive(),
                card.getCreatedAt()
        );
    }
}
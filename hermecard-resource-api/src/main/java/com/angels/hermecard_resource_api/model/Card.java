package com.angels.hermecard_resource_api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    private Long id;

    @Column("owner_username")
    private String ownerUsername;

    @Column("name")
    private String name;

    @Column("brand")
    private String brand;

    // "limit" é palavra reservada no PostgreSQL — mapeado como card_limit
    @Column("card_limit")
    private BigDecimal limit;

    @Column("current_balance")
    private BigDecimal currentBalance;

    @Column("closing_day")
    private Integer closingDay;

    @Column("due_day")
    private Integer dueDay;

    @Builder.Default
    @Column("active")
    private Boolean active = true;

    @Builder.Default
    @Column("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
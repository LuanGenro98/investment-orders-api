package com.broker.orders.web;

import java.math.BigDecimal;

/**
 * Usamos Record (Java 14+) para criar um DTO imutável de forma limpa.
 * O Jackson (conversor JSON do Spring) sabe desserializar Records nativamente.
 */
public record OrderRequest(
        String accountId,
        String ticker,
        Integer quantity,
        BigDecimal price,
        String idempotencyKey
) {}

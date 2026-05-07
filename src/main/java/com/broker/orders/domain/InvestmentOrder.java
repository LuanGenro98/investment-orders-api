package com.broker.orders.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representando nossa ordem de investimento.
 * CONCEITO EXAME: O Spring Data JPA varre o classpath (Entity Scan)
 * procurando classes anotadas com @Entity para mapear para o banco relacional.
 */
@Entity
@Table(name = "investment_orders")
public class InvestmentOrder {

    /**
     * @Id: Define a chave primária.
     * @GeneratedValue: Delega a geração do ID para o banco de dados.
     * CONCEITO EXAME: A estratégia IDENTITY significa que o banco (ex: auto-increment do H2/MySQL)
     * gerará o valor. O Hibernate não precisa fazer um select antes de inserir.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;
    private Integer quantity;
    private BigDecimal price;
    private String status;
    private String idempotencyKey;
    private LocalDateTime createdAt;

    /**
     * CONCEITO EXAME (MUITO IMPORTANTE):
     * O JPA exige OBRIGATORIAMENTE um construtor sem argumentos (no-args constructor).
     * O Hibernate/JPA usa Reflection (Class.newInstance()) para instanciar essa classe
     * quando traz os dados do banco, antes de popular os campos via setters ou reflection direta.
     */
    protected InvestmentOrder() {}

    /**
     * Construtor focado no negócio. Usado por nós na aplicação para criar novas ordens.
     */
    public InvestmentOrder(String ticker, Integer quantity, BigDecimal price, String idempotencyKey) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.status = "PENDING";
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public String getStatus() { return status; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setter apenas para o que faz sentido mudar na regra de negócio
    public void setStatus(String status) {
        this.status = status;
    }
}
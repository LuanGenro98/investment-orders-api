package com.broker.orders.repository;

import com.broker.orders.domain.InvestmentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * CONCEITO EXAME: Spring Data Repository Abstraction.
 * Você não implementa essa interface! O Spring cria um Proxy Dinâmico (Dynamic Proxy)
 * em tempo de execução que intercepta as chamadas e gera o SQL via Hibernate.
 *
 * POR QUE NÃO PRECISA DE @Repository AQUI?
 * Interfaces que estendem JpaRepository já são registradas automaticamente como Beans
 * pelo Spring Data. A implementação proxy gerada em runtime já possui a anotação @Repository
 * (que também traduz exceções de banco de dados para DataAccessException do Spring).
 */
public interface OrderRepository extends JpaRepository<InvestmentOrder, Long> {

    /**
     * CONCEITO EXAME: Query Methods (Derived Queries).
     * O Spring faz o parse do nome do método ("findBy" + "IdempotencyKey")
     * e monta a query SQL automaticamente: SELECT * FROM investment_orders WHERE idempotency_key = ?
     *
     * Retornar Optional é uma boa prática para evitar NullPointerException.
     */
    Optional<InvestmentOrder> findByIdempotencyKey(String idempotencyKey);
}
package com.broker.orders.service;

import com.broker.orders.domain.InvestmentOrder;
import com.broker.orders.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CONCEITO EXAME: @SpringBootTest procura a classe principal anotada com @SpringBootApplication
 * e usa ela para iniciar o ApplicationContext completo do Spring para o teste.
 */
@SpringBootTest
public class OrderServiceIntegrationTest {

    /**
     * Em testes de integração, usamos @Autowired direto nos campos (Field Injection)
     * por conveniência, já que o framework de testes é quem instancia a classe, não nós.
     */
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        // Limpamos o banco antes de cada teste para garantir isolamento
        orderRepository.deleteAll();
    }

    @Test
    void shouldProcessOrderSuccessfully() {
        // Cenário: Compra de 100 cotas de MXRF11 a R$ 10,00 (Total R$ 1000 - dentro do limite)
        String idempotencyKey = "IDEMP-001";

        InvestmentOrder order = orderService.placeOrder("ACC-123", "MXRF11", 100, new BigDecimal("10.00"), idempotencyKey);

        assertNotNull(order.getId());
        assertEquals("PENDING", order.getStatus());
        assertEquals(1, orderRepository.count(), "A ordem deve ser persistida no banco");
    }

    @Test
    void shouldRollbackWhenAccountBalanceLimitIsExceeded() {
        // Cenário: Compra de 1000 ações de ITSA4 a R$ 15,00 (Total R$ 15.000 - excede o limite de R$ 10.000)
        String idempotencyKey = "IDEMP-002";

        // CONCEITO EXAME: assertThrows verifica se a exceção esperada foi lançada
        assertThrows(IllegalArgumentException.class,
                () -> orderService.placeOrder("ACC-123", "ITSA4", 1000, new BigDecimal("15.00"), idempotencyKey));

        // A PROVA DE FOGO DO ROLLBACK:
        // Como o AccountService lançou uma RuntimeException, o @Transactional do OrderService
        // TEM que ter desfeito a operação. O banco deve estar vazio!
        assertEquals(0, orderRepository.count(), "A transação DEVE sofrer rollback e não salvar a ordem");
    }

    @Test
    void shouldBlockDuplicateOrderDueToIdempotencyAspect() {
        // Cenário: Cliente envia a mesma ordem duas vezes por instabilidade de rede
        String idempotencyKey = "IDEMP-003";

        // Primeira chamada (Sucesso)
        orderService.placeOrder("ACC-123", "KNCR11", 50, new BigDecimal("100.00"), idempotencyKey);

        // Segunda chamada com a mesma chave (Deve ser interceptada pelo AOP)
        assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder("ACC-123", "KNCR11", 50, new BigDecimal("100.00"), idempotencyKey);
        });

        // Verificamos que apenas UMA ordem existe no banco
        assertEquals(1, orderRepository.count(), "O aspecto de idempotência deve barrar a duplicação");
    }
}

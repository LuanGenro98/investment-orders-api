package com.broker.orders.service;

import com.broker.orders.aop.Idempotent;
import com.broker.orders.domain.InvestmentOrder;
import com.broker.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * @Service é um "Stereotype Annotation" (assim como @Component, @Repository, @Controller).
 * CONCEITO EXAME: Durante o Component Scan, o Spring IoC Container encontra essa classe
 * e cria um Bean (uma instância gerenciada) dela.
 * VALOR DEFAULT: Por padrão, todo Bean no Spring é SINGLETON (uma única instância para toda a aplicação).
 * Como é Singleton, classes @Service NÃO DEVEM ter estado (stateful properties) que mudam por requisição,
 * para evitar problemas de concorrência (Thread-safety).
 */
@Service
public class OrderService {

    /**
     * O repositório é declarado como 'final' para garantir a imutabilidade após a injeção.
     */
    private final OrderRepository orderRepository;

    /**
     * CONCEITO EXAME: Injeção de Dependência via Construtor (Constructor Injection).
     * Esta é a forma recomendada pela equipe do Spring.
     *
     * CADÊ O @Autowired?
     * A partir do Spring 4.3, se a classe tiver apenas UM construtor, o @Autowired
     * é IMPLÍCITO. O Spring resolve a dependência automaticamente.
     * Vantagem cobrada na prova: Permite que a classe seja instanciada em testes unitários puros
     * usando a palavra 'new' passando um Mock, sem precisar subir o contexto do Spring.
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Agora, o Spring vai criar um Proxy em volta de OrderService.
     * Quando alguém chamar placeOrder, na verdade estará chamando o Proxy,
     * que vai executar o nosso IdempotencyAspect primeiro.
     */
    @Idempotent
    public InvestmentOrder placeOrder(String ticker, Integer quantity, java.math.BigDecimal price, String idempotencyKey) {

        InvestmentOrder newOrder = new InvestmentOrder(ticker, quantity, price, idempotencyKey);

        // O proxy do JpaRepository intercepta o .save() e executa o INSERT
        return orderRepository.save(newOrder);
    }
}
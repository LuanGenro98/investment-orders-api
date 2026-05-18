package com.broker.orders.service;

import com.broker.orders.aop.Idempotent;
import com.broker.orders.domain.InvestmentOrder;
import com.broker.orders.repository.OrderRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

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

    private final AccountService accountService;

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
    public OrderService(OrderRepository orderRepository, AccountService accountService) {
        this.orderRepository = orderRepository;
        this.accountService = accountService;
    }

    /**
     * Agora, o Spring vai criar um Proxy em volta de OrderService.
     * Quando alguém chamar placeOrder, na verdade estará chamando o Proxy,
     * que vai executar o nosso IdempotencyAspect primeiro.
     *
     * CONCEITO EXAME: @Transactional cria um proxy em volta do método.
     * Ele inicia uma transação (connection.setAutoCommit(false)) antes do método executar,
     * e faz o COMMIT se o método terminar com sucesso.
     *
     * SE der erro, ele faz o ROLLBACK das operações no banco.
     * OBJECTIVE 5.3: Method-level Security & OBJECTIVE 1.3.3: SpEL.
     *
     * Aqui nós usamos o SpEL (#accountId) para pegar o parâmetro do método,
     * e 'authentication.name' para pegar o nome de usuário do contexto de segurança atual.
     * Se eles não baterem, o Spring lança uma AccessDeniedException ANTES do método executar.
     */
    @Idempotent
    @Transactional
    @PreAuthorize("#accountId == authentication.name or hasRole('ADMIN')")
    public InvestmentOrder placeOrder(String accountId, String ticker, Integer quantity, BigDecimal price, String idempotencyKey) {

        BigDecimal totalAmount = price.multiply(new BigDecimal(quantity));

        // Passo 1: Deduz o saldo (pode lançar IllegalArgumentException)
        accountService.deductBalance(accountId, totalAmount);

        // Passo 2: Salva a ordem
        InvestmentOrder newOrder = new InvestmentOrder(ticker, quantity, price, idempotencyKey);

        // O proxy do JpaRepository intercepta o .save() e executa o INSERT
        return orderRepository.save(newOrder);
    }

    @Transactional(readOnly = true)
    public Optional<InvestmentOrder> getOrder(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
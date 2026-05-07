package com.broker.orders.aop;

import com.broker.orders.repository.OrderRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * CONCEITO EXAME:
 * @Aspect: Diz ao Spring que esta classe contém conselhos (Advices) e regras de corte (Pointcuts).
 * @Component: IMPORTANTE! O @Aspect por si só NÃO cria um Bean. Você DEVE usar @Component
 * para que o Spring IoC Container gerencie essa classe.
 */
@Aspect
@Component
public class IdempotencyAspect {

    private final OrderRepository orderRepository;

    public IdempotencyAspect(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * CONCEITO EXAME - ADVICE TYPES:
     * @Before: Executa antes do método.
     * @After: Executa depois (independente de sucesso ou erro).
     * @AfterReturning: Executa só se o método der sucesso.
     * @AfterThrowing: Executa só se o método lançar exceção.
     * @Around: O mais poderoso! Envolve o método. Você decide se o método original vai rodar ou não.
     *
     * POINTCUT EXPRESSION: "@annotation(com.broker.orders.aop.Idempotent)"
     * Significa: "Intercepte qualquer execução de método que possua esta anotação".
     */
    @Around("@annotation(com.broker.orders.aop.Idempotent)")
    public Object checkIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {

        // CONCEITO EXAME - JOINPOINT: Representa o ponto exato da execução (o método interceptado).
        // Aqui, extraímos os argumentos passados para o método placeOrder().
        Object[] args = joinPoint.getArgs();
        String idempotencyKey = null;

        // Procuramos a string que atua como chave de idempotência nos argumentos
        for (Object arg : args) {
            if (arg instanceof String && ((String) arg).startsWith("IDEMP-")) {
                idempotencyKey = (String) arg;
                break;
            }
        }

        if (idempotencyKey != null) {
            // Regra de negócio isolada: Verifica se a ordem já existe no banco
            boolean orderExists = orderRepository.findByIdempotencyKey(idempotencyKey).isPresent();

            if (orderExists) {
                // Se a ordem já existe, BARRÁMOS a execução do método original!
                throw new IllegalStateException("Ordem já processada para a chave: " + idempotencyKey);
            }
        }

        // Se chegou até aqui, a chave é inédita.
        // joinPoint.proceed() é o comando que diz: "Pode executar o método original agora".
        return joinPoint.proceed();
    }
}

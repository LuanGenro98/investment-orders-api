package com.broker.orders.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CONCEITO EXAME: Criação de anotações customizadas.
 *
 * @Target(ElementType.METHOD): Indica que esta anotação só pode ser colocada em MÉTODOS.
 * @Retention(RetentionPolicy.RUNTIME): ESSENCIAL! Garante que a anotação estará disponível
 * em tempo de execução para que o Spring AOP (usando Reflection) consiga encontrá-la.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {
    // Não precisamos de propriedades internas por enquanto. A mera presença dela já é o gatilho.
}

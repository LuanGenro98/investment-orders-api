package com.broker.orders.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * OBJECTIVE 1.4.4: "Stereotype" Annotations.
 * @Component é a anotação genérica. (@Service, @Repository e @Controller são meta-anotações
 * que incluem @Component internamente).
 */
@Component
public class TickerValidator implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(TickerValidator.class);

    private boolean isReady;

    public TickerValidator() {
        log.info("1. Instanciação: Construtor chamado. isReady = {}", isReady);
    }

    /**
     * OBJECTIVE 1.4.3: Use @PostConstruct and @PreDestroy
     * Edge Case do Exame: Quem roda primeiro, @PostConstruct ou afterPropertiesSet()?
     * Resposta: @PostConstruct sempre roda ANTES. Ele é processado por um BeanPostProcessor
     * (CommonAnnotationBeanPostProcessor) antes das interfaces de ciclo de vida do próprio Spring.
     */
    @PostConstruct
    public void init() {
        log.info("2. @PostConstruct: Preparando lista de ativos válidos...");
        this.isReady = true;
    }

    /**
     * OBJECTIVE 1.5.1: Spring Bean Lifecycle
     * Interface InitializingBean obriga a implementação deste método.
     * Padrão (Default): É desencorajado usar interfaces nativas do Spring atreladas à sua regra de negócio
     * (acoplamento forte). @PostConstruct é a recomendação (JSR-250, padrão Java).
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("3. InitializingBean.afterPropertiesSet(): TickerValidator está pronto e configurado.");
    }

    /**
     * Método de negócio
     */
    public boolean isValid(String ticker) {
        if (!isReady) {
            log.error("Tentativa de uso do validador antes da inicialização!");
            throw new IllegalStateException("Validador não inicializado!");
        }

        // Uso de placeholder '{}' para melhor performance de log
        log.debug("Validando ticker da ordem: {}", ticker);
        return ticker.matches("^[A-Z]{4}[0-9]{1,2}$");
    }

    /**
     * Roda antes do bean ser destruído (ex: quando a aplicação é encerrada).
     */
    @PreDestroy
    public void cleanup() {
        log.info("4. @PreDestroy: Limpando recursos e cache de ativos...");
    }

    @Override
    public void destroy() throws Exception {
        log.info("5. DisposableBean.destroy(): Fechando conexões finais...");
    }
}

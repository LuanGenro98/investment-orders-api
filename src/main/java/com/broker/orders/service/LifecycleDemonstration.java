package com.broker.orders.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LifecycleDemonstration implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(LifecycleDemonstration.class);

    // Dependência para testarmos quando ela fica disponível
    private AccountService accountService;

    // 1. DOMÍNIO JAVA: Bloco Estático
    static {
        log.info(">>> 1. [JAVA] Bloco Estático: A classe foi carregada pela JVM.");
    }

    // 2. DOMÍNIO JAVA: Bloco de Inicialização de Instância
    {
        log.info(">>> 2. [JAVA] Bloco de Instância: Preparando para rodar o construtor.");
    }

    // 3. DOMÍNIO JAVA/SPRING: Construtor
    public LifecycleDemonstration() {
        log.info(">>> 3. [JAVA] Construtor: Objeto instanciado. O AccountService agora é: {}", this.accountService);
        // EDGE CASE DE PROVA: Se você tentar usar this.accountService.metodo() aqui,
        // vai tomar NullPointerException, porque o Spring ainda não fez a injeção via setter/field!
    }

    // 4. DOMÍNIO SPRING: Injeção de Dependências (Setter)
    @Autowired
    public void setAccountService(AccountService accountService) {
        log.info(">>> 4. [SPRING] Injeção de Dependência: Spring chamou o Setter e injetou o AccountService.");
        this.accountService = accountService;
    }

    // (O BeanPostProcessor BEFORE roda silenciosamente aqui, interceptando o fluxo)

    // 5. DOMÍNIO SPRING: @PostConstruct
    @PostConstruct
    public void init() {
        log.info(">>> 5. [SPRING] @PostConstruct: O Bean está montado. O AccountService é nulo? {}", (this.accountService == null));
    }

    // 6. DOMÍNIO SPRING: InitializingBean
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(">>> 6. [SPRING] afterPropertiesSet: Interface nativa do Spring rodando.");
    }

    // (O BeanPostProcessor AFTER roda silenciosamente aqui e decide se cria um Proxy)
}

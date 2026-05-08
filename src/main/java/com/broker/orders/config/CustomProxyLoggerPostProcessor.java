package com.broker.orders.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * OBJECTIVE 1.5.2: Use a BeanPostProcessor
 * Ao anotar com @Component, o Spring detecta que esta classe implementa BeanPostProcessor
 * e a registra automaticamente na esteira de criação de TODOS os outros beans.
 */
@Component
public class CustomProxyLoggerPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(CustomProxyLoggerPostProcessor.class);

    /**
     * Fase 1: Executa ANTES do @PostConstruct e do afterPropertiesSet() do bean.
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // Vamos filtrar para não poluir o console com os beans internos do Spring Boot
        if (beanName.equals("orderService")) {
            log.info("--- BPP BeforeInit: Inspecionando '{}' | Classe: {}", beanName, bean.getClass().getSimpleName());
        }

        // CONCEITO EXAME: Você DEVE retornar o bean. Se retornar null, você quebra o container.
        return bean;
    }

    /**
     * Fase 2: Executa DEPOIS da inicialização (depois do @PostConstruct).
     * OBJECTIVE 1.5.3: Explain how Spring proxies add behavior at runtime.
     * É exatamente neste método que as classes de AOP do Spring olham para o bean e dizem:
     * "Tem @Transactional? Tem @Idempotent? Então vou envelopar esse cara num Proxy!"
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (beanName.equals("orderService")) {
            // AopUtils é uma classe utilitária fantástica do Spring
            boolean isProxy = AopUtils.isAopProxy(bean);

            log.info("--- BPP AfterInit: O bean '{}' é um Proxy? {}", beanName, isProxy);

            if (isProxy) {
                // Aqui você verá que a classe não é mais OrderService, mas sim um gerado pelo CGLIB
                log.info("--- BPP AfterInit: Nome real da classe gerada em runtime: {}", bean.getClass().getName());
            }
        }
        return bean;
    }
}

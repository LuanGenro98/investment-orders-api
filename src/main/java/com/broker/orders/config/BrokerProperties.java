package com.broker.orders.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * OBJECTIVE 1.3.1: Use External Properties to control Configuration
 * Lemos tudo que começar com "broker.limits" no application.properties
 */
@Component
@ConfigurationProperties(prefix = "broker.limits")
public class BrokerProperties {

    // Mapeia "broker.limits.max-order-value"
    private BigDecimal maxOrderValue = new BigDecimal("50000"); // Valor Default de segurança

    // Mapeia "broker.limits.allowed-tickers"
    private String[] allowedTickers;

    // Getters e Setters são OBRIGATÓRIOS para o @ConfigurationProperties funcionar
    public BigDecimal getMaxOrderValue() { return maxOrderValue; }
    public void setMaxOrderValue(BigDecimal maxOrderValue) { this.maxOrderValue = maxOrderValue; }
    public String[] getAllowedTickers() { return allowedTickers; }
    public void setAllowedTickers(String[] allowedTickers) { this.allowedTickers = allowedTickers; }
}

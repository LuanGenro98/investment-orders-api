package com.broker.orders.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * OBJECTIVE 6.3.4: Define custom health indicators.
 * Ao implementar HealthIndicator e anotar com @Component, o Actuator
 * automaticamente embute o resultado disso na rota /actuator/health.
 */
@Component
public class B3ExchangeHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Simulando uma checagem: A B3 fecha após as 18h
        LocalTime now = LocalTime.now();
        boolean isExchangeOpen = now.isBefore(LocalTime.of(18, 0));

        if (isExchangeOpen) {
            return Health.up()
                    .withDetail("B3_Status", "Operacional")
                    .withDetail("Latency", "12ms")
                    .build();
        } else {
            // Se a bolsa estiver fechada, nosso sistema fica "OUT_OF_SERVICE"
            return Health.outOfService()
                    .withDetail("B3_Status", "Fechada para negociações")
                    .withDetail("Resume_At", "09:00 AM amanhã")
                    .build();
        }
    }
}
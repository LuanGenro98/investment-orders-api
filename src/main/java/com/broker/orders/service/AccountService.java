package com.broker.orders.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class AccountService {

    /**
     * Simula a dedução de saldo de uma conta.
     * Na vida real, isso chamaria um AccountRepository.
     */
    public void deductBalance(String accountId, BigDecimal amount) {
        System.out.println("Deduzindo R$ " + amount + " da conta " + accountId);

        // Simulando uma regra de negócio: não permite ordens acima de 10.000 de uma vez
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            // CONCEITO EXAME: Lançando uma RuntimeException (Unchecked Exception)
            throw new IllegalArgumentException("Saldo insuficiente ou limite excedido.");
        }
    }
}

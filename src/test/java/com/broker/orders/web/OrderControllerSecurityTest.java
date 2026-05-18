package com.broker.orders.web;

import com.broker.orders.config.SecurityConfig;
import com.broker.orders.domain.InvestmentOrder;
import com.broker.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OBJECTIVE 4.1.3 / 4.2.4: Fatiamento + Importação Explícita.
 * Sem o @Import, o @WebMvcTest ignora o nosso SecurityConfig e ativa o CSRF padrão!
 */
@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    /**
     * CENÁRIO 1: O "Desavisado" (401 Unauthorized)
     * Não usamos nenhuma anotação de segurança. O filtro do Spring barra a requisição
     * antes mesmo de encostar no Controller.
     */
    @Test
    void shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        String jsonPayload = """
                { "accountId": "ACC-123", "ticker": "MXRF11", "quantity": 100, "price": 10.00 }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                // Exige 401 Unauthorized
                .andExpect(status().isUnauthorized());
    }

    /**
     * CENÁRIO 2: O "Cliente Padrão" (201 Created)
     * OBJECTIVE 5.2 / 4.2.3: @WithMockUser
     * Esta anotação cria um "SecurityContext" falso. Ela diz ao Spring:
     * "Finja que existe um usuário chamado 'cliente' com a role 'USER' logado neste exato momento."
     */
    @Test
    @WithMockUser(username = "ACC-123", roles = "USER")
    void shouldCreateOrderWhenUserIsAuthenticated() throws Exception {

        // Stub do Mockito omitido para brevidade (igual ao anterior)
        InvestmentOrder mockReturnedOrder = new InvestmentOrder("MXRF11", 100, new BigDecimal("10.00"), "IDEMP-123");
        Mockito.when(orderService.placeOrder(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockReturnedOrder);

        String jsonPayload = """
                { "accountId": "ACC-123", "ticker": "MXRF11", "quantity": 100, "price": 10.00, "idempotencyKey": "IDEMP-123" }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                // Como o @WithMockUser proveu as credenciais corretas, o filtro libera (201)
                .andExpect(status().isCreated());
    }

    /**
     * CENÁRIO 3: A "Cerca Elétrica" da Autorização (403 Forbidden)
     * Lembra que no nosso SecurityConfig nós bloqueamos requisições DELETE para qualquer um
     * que não tenha a role "ADMIN"? Vamos testar a Autorização agora.
     */
    @Test
    @WithMockUser(username = "cliente", roles = "USER") // Tem auth, mas NÃO tem autorização
    void shouldReturn403WhenUserTriesToDeleteWithoutAdminRole() throws Exception {

        mockMvc.perform(delete("/api/v1/orders/1"))
                // O usuário existe e está logado (não é 401), mas não tem a patente para a ação (403)
                .andExpect(status().isForbidden());
    }
}
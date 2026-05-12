package com.broker.orders.web;

import com.broker.orders.domain.InvestmentOrder;
import com.broker.orders.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

// Importações estáticas vitais para a fluência do MockMvc (Cai muito na leitura de código da prova)
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OBJECTIVE 4.2.4: Perform slice testing
 * Ao passar a classe do Controller, dizemos ao Spring: "Suba APENAS o OrderController".
 */
@WebMvcTest(OrderController.class)
public class OrderControllerWebMvcTest {

    /**
     * OBJECTIVE 4.2.3: Perform MockMVC testing
     * O MockMvc é o ponto de entrada para despachar requisições HTTP simuladas
     * direto para o DispatcherServlet, sem iniciar um servidor web real (Tomcat).
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * CONCEITO EXAME: @MockBean vs @Mock
     * @Mock (do Mockito puro) cria um mock isolado que o Spring desconhece.
     * @MockBean (do Spring Boot) cria o mock E o registra como um Bean no ApplicationContext,
     * substituindo qualquer Bean real de mesmo tipo caso exista, ou suprindo a dependência faltante.
     */
//    @MockBean
    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrderAndReturn201Created() throws Exception {

        // 1. Preparação (Stubbing) do comportamento do nosso Mock
        String idempotencyKey = "IDEMP-TEST-001";
        InvestmentOrder mockReturnedOrder = new InvestmentOrder("MXRF11", 100, new BigDecimal("10.00"), idempotencyKey);

        // Usamos reflection ou um setter de teste (se tivéssemos) para simular o ID gerado pelo banco
        // Para simplificar sem ferir o encapsulamento, vamos assumir que o ID retornado na URI seja null/vazio ou mockamos a resposta

        Mockito.when(orderService.placeOrder(
                Mockito.eq("ACC-123"),
                Mockito.eq("MXRF11"),
                Mockito.eq(100),
                Mockito.eq(new BigDecimal("10.00")),
                Mockito.eq(idempotencyKey)
        )).thenReturn(mockReturnedOrder);

        // Payload JSON idêntico ao do nosso curl
        String jsonPayload = """
                {
                  "accountId": "ACC-123",
                  "ticker": "MXRF11",
                  "quantity": 100,
                  "price": 10.00,
                  "idempotencyKey": "IDEMP-TEST-001"
                }
                """;

        // 2. Execução e Validação fluente via MockMvc
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                // Validações esperadas no exame:
                .andExpect(status().isCreated()) // Espera HTTP 201
                .andExpect(header().exists("Location")) // Espera o Header Location
                .andExpect(jsonPath("$.ticker").value("MXRF11")) // Inspeciona o JSON de retorno
                .andExpect(jsonPath("$.status").value("PENDING"));

        // 3. Verificação de que o Controller realmente delegou para o Service
        Mockito.verify(orderService, Mockito.times(1)).placeOrder(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.any(), Mockito.anyString()
        );
    }
}
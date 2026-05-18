package com.broker.orders.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class B3MarketClient {

    private final RestTemplate restTemplate;

    public B3MarketClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * MÉTODO 1: getForObject()
     * Foco do Exame: Retorna DIRETAMENTE o payload (corpo da resposta) mapeado para um objeto.
     * Você perde o acesso ao status code e aos headers HTTP. Se a API retornar 404,
     * o RestTemplate lança uma exceção.
     */
    public BigDecimal fetchCurrentPriceDirectly(String ticker) {
        String url = "https://api.b3.simulacao.com/v1/quotes/" + ticker;

        // Simulação: A API externa retorna apenas um número decimal plain-text ou JSON simples
        return restTemplate.getForObject(url, BigDecimal.class);
    }

    /**
     * MÉTODO 2: getForEntity()
     * Foco do Exame: Retorna um ResponseEntity. Use isso quando você PRECISAR
     * inspecionar o HTTP Status Code ou ler um cabeçalho específico antes de pegar o corpo.
     */
    public BigDecimal fetchCurrentPriceWithHeaders(String ticker) {
        String url = "https://api.b3.simulacao.com/v1/quotes/" + ticker;

        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(url, BigDecimal.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new IllegalStateException("Falha ao comunicar com a bolsa.");
    }

    /**
     * MÉTODO 3: exchange()
     * Foco do Exame: O método mais poderoso. É o ÚNICO jeito de enviar cabeçalhos HTTP
     * customizados (como um Bearer Token) em uma requisição GET.
     */
    public BigDecimal fetchPriceSecurely(String ticker, String bearerToken) {
        String url = "https://api.b3.simulacao.com/v1/quotes/" + ticker;

        // Montando o header com o Token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + bearerToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // O exchange exige: URL, Verbo HTTP, HttpEntity (request), Tipo de Retorno
        ResponseEntity<BigDecimal> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                BigDecimal.class
        );

        return response.getBody();
    }
}
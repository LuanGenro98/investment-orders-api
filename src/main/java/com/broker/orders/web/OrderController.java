package com.broker.orders.web;

import com.broker.orders.domain.InvestmentOrder;
import com.broker.orders.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * OBJECTIVE 1.4.4 & 3.1.3: @RestController
 * EDGE CASE EXAME: Qual a diferença entre @Controller e @RestController?
 * Resposta: @RestController é uma anotação de conveniência que combina @Controller e @ResponseBody.
 * O @ResponseBody diz ao DispatcherServlet: "Não tente procurar uma página HTML (View) para renderizar,
 * pegue o meu retorno e grave direto no corpo da resposta HTTP (em JSON)".
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    // Injeção via construtor, como manda a cartilha!
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * OBJECTIVE 3.2.1: Controllers to support REST endpoints for various verbs (POST)
     */
    @PostMapping
    public ResponseEntity<InvestmentOrder> createOrder(@RequestBody OrderRequest request) {

        // Chamamos o serviço (que agora sabemos que é um Proxy com @Transactional e @Idempotent)
        InvestmentOrder createdOrder = orderService.placeOrder(
                request.accountId(),
                request.ticker(),
                request.quantity(),
                request.price(),
                request.idempotencyKey()
        );

        // CONCEITO EXAME (Boas Práticas REST):
        // Quando criamos um recurso, devemos retornar HTTP 201 (Created) e o header 'Location'
        // com a URI do novo recurso criado.
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdOrder.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdOrder);
    }

    /**
     * OBJECTIVE 3.1.3: Create a simple RESTful controller to handle GET requests
     * (Adicione um método findById no seu OrderRepository e no OrderService para isso compilar)
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvestmentOrder> getOrder(@PathVariable Long id) {
         return orderService.getOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
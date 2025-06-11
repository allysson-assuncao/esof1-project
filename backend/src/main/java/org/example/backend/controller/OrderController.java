package org.example.backend.controller;

import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.OrderRequestDTO;
import org.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/app/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerOrder(@RequestBody OrderRequestDTO request) {
        return this.orderService.registerOrder(request) ?
                ResponseEntity.status(HttpStatus.OK).body("Pedido registrado com sucesso") :
                ResponseEntity.badRequest().body("Algo deu errado! Tente novamente.");
    }

    @GetMapping("/by-tab/{guestTabId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByGuestTabId(@PathVariable Long guestTabId) {
        List<OrderDTO> orders = orderService.getOrdersByGuestTabId(guestTabId);
        return ResponseEntity.ok(orders);
    }


}

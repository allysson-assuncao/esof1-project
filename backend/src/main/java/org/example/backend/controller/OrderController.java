package org.example.backend.controller;

import org.example.backend.dto.Order.DetailedOrderDTO;
import org.example.backend.dto.Order.SimpleOrderDTO;
import org.example.backend.dto.Order.OrderDTO;
import org.example.backend.dto.Order.OrderRequestDTO;
import org.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/queue")
    public ResponseEntity<List<DetailedOrderDTO>> getQueue() {
        return ResponseEntity.ok(this.orderService.getQueue());
    }

    @GetMapping("/by-tab/{guestTabId}")
    public ResponseEntity<List<DetailedOrderDTO>> selectOrderByGuestTabId(@PathVariable Long guestTabId) {
        return ResponseEntity.ok(this.orderService.selectOrdersByGuestTabId(guestTabId));
    }

    @GetMapping("/select-all/{localTableID}")
    public ResponseEntity<List<SimpleOrderDTO>> selectOrdersByLocalTableId(@PathVariable UUID localTableID) {
        return ResponseEntity.ok(this.orderService.selectOrdersByLocalTableId(localTableID));
    }

    @GetMapping("/get/all")
    public List<OrderDTO> getAllOrders() {
        return this.orderService.getAllOrders();
    }

    /*@GetMapping("/get/in-prepare/bar")
    public List<OrderDTO> getOrdersInPrepareAndBar() {
        return this.orderService.getOrdersInPrepareAndBar();
    }

    @GetMapping("/get/in-prepare/kitchen")
    public List<OrderDTO> getOrdersInPrepareAndKitchen() {
        return this.orderService.getOrdersInPrepareAndKitchen();
    }*/

}

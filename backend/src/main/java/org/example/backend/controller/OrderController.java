package org.example.backend.controller;

import org.example.backend.dto.Order.*;
import org.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    @PostMapping("/{orderId}/advance")
    public ResponseEntity<Void> advanceOrderStatus(@PathVariable Long orderId) {
        orderService.advanceStatus(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/regress")
    public ResponseEntity<Void> regressOrderStatus(@PathVariable Long orderId) {
        orderService.regressStatus(orderId);
        return ResponseEntity.ok().build();
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


    /*@PostMapping("/filter")
    public ResponseEntity<OrderKanbanResponseDTO> getKanbanData(
            @RequestBody OrderKanbanFilterDTO filterDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "orderedTime") String orderBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        OrderKanbanResponseDTO response = this.orderService.getOrdersForKanban(filterDTO, page, size, orderBy, direction);
        return ResponseEntity.ok(response);
    }*/

}

package org.example.backend.controller;

import org.example.backend.dto.SimpleOrderDTO;
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
    public ResponseEntity<?> registerCategory(@RequestBody String request) {
        return this.orderService.registerOrder(request) ?
                ResponseEntity.status(HttpStatus.OK).body("") :
                ResponseEntity.badRequest().body("");
    }

    @GetMapping("/select-all/{localTableID}")
    public ResponseEntity<List<SimpleOrderDTO>> selectOrdersByLocalTableId(@PathVariable UUID localTableID) {
        return ResponseEntity.ok(this.orderService.selectOrdersByLocalTableId(localTableID));
    }

}

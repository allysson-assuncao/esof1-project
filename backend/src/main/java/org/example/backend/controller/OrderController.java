package org.example.backend.controller;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.OrderFilterDTO;
import org.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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

    @PostMapping("/filter")
    public ResponseEntity<FilteredPageDTO<OrderDTO>> filterOutputFiles(
            @RequestBody OrderFilterDTO filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        System.out.println(filterDto.toString());
        Page<OrderDTO> ordersPage = this.orderService.getOrdersByFilters(filterDto, page, size, orderBy, direction);
        return ResponseEntity.ok(new FilteredPageDTO<>(ordersPage.getContent(), ordersPage.getTotalPages()));
    }

}

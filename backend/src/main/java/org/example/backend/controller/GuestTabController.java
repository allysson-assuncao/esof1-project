package org.example.backend.controller;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.GuestTabDTO;
import org.example.backend.dto.OrderDTO;
import org.example.backend.dto.OrderFilterDTO;
import org.example.backend.service.GuestTabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/guest-tab")
public class GuestTabController {

    private final GuestTabService guestTabService;

    @Autowired
    public GuestTabController(GuestTabService guestTabService) {
        this.guestTabService = guestTabService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCategory(@RequestBody String request) {
        return this.guestTabService.registerGuestTap(request) ?
                ResponseEntity.status(HttpStatus.OK).body("") :
                ResponseEntity.badRequest().body("");
    }

    /*@PostMapping("/filter")
    public ResponseEntity<FilteredPageDTO<GuestTabDTO>> filterOutputFiles(
            @RequestBody OrderFilterDTO filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        System.out.println(filterDto.toString());
        System.out.println("Received");
        Page<OrderDTO> ordersPage = this.orderService.getOrdersByFilters(filterDto, page, size, orderBy, direction);
        return ResponseEntity.ok(new FilteredPageDTO<>(ordersPage.getContent(), ordersPage.getTotalPages()));
    }*/

}

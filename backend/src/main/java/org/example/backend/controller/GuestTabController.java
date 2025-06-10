package org.example.backend.controller;

import org.example.backend.dto.FilteredPageDTO;
import org.example.backend.dto.GuestTabDTO;
import org.example.backend.dto.GuestTabFilterDTO;
import org.example.backend.dto.SimpleGuestTabDTO;
import org.example.backend.service.GuestTabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
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

    @GetMapping("/select-all")
    public ResponseEntity<List<SimpleGuestTabDTO>> selectAllGuestTabs() {
        return ResponseEntity.ok(this.guestTabService.selectAllGuestTabs());
    }

    @PostMapping("/filter")
    public ResponseEntity<FilteredPageDTO<GuestTabDTO>> filterGuestTabs(
            @RequestBody GuestTabFilterDTO filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String orderBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        System.out.println(filterDto.toString());
        Page<GuestTabDTO> guestTabPage = this.guestTabService.getGuestTabByFilters(filterDto, page, size, orderBy, direction);
        return ResponseEntity.ok(new FilteredPageDTO<>(guestTabPage.getContent(), guestTabPage.getTotalPages()));
    }

}

package org.example.backend.controller;

import org.example.backend.dto.*;
import org.example.backend.dto.GuestTab.*;
import org.example.backend.service.GuestTabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/app/guest-tab")
public class GuestTabController {

    private final GuestTabService guestTabService;

    @Autowired
    public GuestTabController(GuestTabService guestTabService) {
        this.guestTabService = guestTabService;
    }

    @GetMapping("/all-tabs")
    public List<GuestTabGetDTO> getGuestTabs() {
        return guestTabService.getGuestTabs();
    }

    //Acessar {{host}}/app/guest-tab/tabs
    @GetMapping("/{tableNumber}")
    public List<GuestTabGetDTO> getGuestTabsByTableNumber(@PathVariable int tableNumber) {
        return guestTabService.getGuestTabsByTableNumber(tableNumber);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerGuestTab(@RequestBody GuestTabRequestDTO request) {
        return this.guestTabService.registerGuestTab(request) ?
                ResponseEntity.status(HttpStatus.OK).body("") :
                ResponseEntity.badRequest().body("");
    }

    @GetMapping("/select-all/{localTableID}")
    public ResponseEntity<List<SimpleGuestTabDTO>> selectGuestTabsByLocalTableId(@PathVariable UUID localTableID) {
        return ResponseEntity.ok(this.guestTabService.selectGuestTabsByLocalTableId(localTableID));
    }

    @PutMapping("/close-tab/{tabId}")
    public ResponseEntity<String> closeGuestTab(@PathVariable Long tabId) {
        return ResponseEntity.ok(this.guestTabService.closeTabById(tabId));
    }

    @PostMapping("/filter")
    public ResponseEntity<FilteredPageDTO<GuestTabDTO>> filterGuestTabs(
            @RequestBody GuestTabFilterDTO filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timeOpened") String orderBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Page<GuestTabDTO> guestTabPage = this.guestTabService.getGuestTabByFilters(filterDto, page, size, orderBy, direction);
        return ResponseEntity.ok(new FilteredPageDTO<>(guestTabPage.getContent(), guestTabPage.getTotalPages()));
    }

}

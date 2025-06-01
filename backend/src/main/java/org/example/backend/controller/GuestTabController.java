package org.example.backend.controller;

import org.example.backend.service.GuestTabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}

package org.example.backend.controller;

import org.example.backend.dto.Waiter.SimpleWaiterDTO;
import org.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/app/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/select-all/{localTableID}")
    public ResponseEntity<List<SimpleWaiterDTO>> selectWaitersByLocalTableId(@PathVariable UUID localTableID) {
        return ResponseEntity.ok(this.userService.selectWaitersByLocalTableId(localTableID));
    }

}

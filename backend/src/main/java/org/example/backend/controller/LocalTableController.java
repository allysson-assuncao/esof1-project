package org.example.backend.controller;

import org.example.backend.dto.LocalTableRequestDTO;
import org.example.backend.service.LocalTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/local-table")
public class LocalTableController {

    private final LocalTableService localTableService;

    @Autowired
    public LocalTableController(LocalTableService localTableService) {
        this.localTableService = localTableService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCategory(@RequestBody LocalTableRequestDTO request) {
        return this.localTableService.registerLocalTable(request) ?
                ResponseEntity.status(HttpStatus.OK).body("") :
                ResponseEntity.badRequest().body("");
    }

}

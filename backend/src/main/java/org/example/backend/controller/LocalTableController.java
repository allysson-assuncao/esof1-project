package org.example.backend.controller;

import org.example.backend.dto.LocalTableGetDTO;
import org.example.backend.dto.LocalTableRequestDTO;
import org.example.backend.model.LocalTable;
import org.example.backend.service.LocalTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/app/local-table")
public class LocalTableController {

    private final LocalTableService localTableService;

    @Autowired
    public LocalTableController(LocalTableService localTableService) {
        this.localTableService = localTableService;
    }

    @GetMapping("/{number}")
    public ResponseEntity<LocalTableGetDTO> findByNumber(@PathVariable int number) {
        var dto = localTableService.findByNumber(number);
        return dto != null ?
                ResponseEntity.ok(dto) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerLocalTable(@RequestBody LocalTableRequestDTO request) {
        boolean created = localTableService.registerLocalTable(request);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Table number " + request.number() + " already exists");
        }
    }
}

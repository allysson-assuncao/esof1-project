package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.backend.dto.LocalTable.LocalTableDTO;
import org.example.backend.dto.LocalTable.LocalTableGetDTO;
import org.example.backend.dto.LocalTable.LocalTableRequestDTO;
import org.example.backend.service.LocalTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/app/local-table")
public class LocalTableController {

    private final LocalTableService localTableService;

    @Autowired
    public LocalTableController(LocalTableService localTableService) {
        this.localTableService = localTableService;
    }

    @GetMapping("/{number}")
    @Operation(summary = "findByNumber – Método que busca um registro por número na tabela local")
    public ResponseEntity<LocalTableGetDTO> findByNumber(@PathVariable int number) {
        var dto = localTableService.findByNumber(number);
        System.out.println("Teste");
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

    @GetMapping("/select-all")
    public ResponseEntity<List<LocalTableDTO>> getGridTables() {
        List<LocalTableDTO> tables = localTableService.getGridTables();
        return ResponseEntity.ok(tables);
    }

}

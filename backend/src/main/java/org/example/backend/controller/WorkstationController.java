package org.example.backend.controller;

import org.example.backend.dto.Order.OrderKanbanFilterDTO;
import org.example.backend.dto.Order.OrderKanbanResponseDTO;
import org.example.backend.dto.Workstation.SimpleWorkstationDTO;
import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
import org.example.backend.model.User;
import org.example.backend.service.WorkstationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/app/workstation")
public class WorkstationController {
    WorkstationService workstationService;

    @Autowired
    public WorkstationController(WorkstationService workstationService) {
        this.workstationService = workstationService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerWorkstation(@RequestBody WorkstationRegisterDTO request) {
        return workstationService.registerWorkstation(request) ?
                ResponseEntity.ok().body("Workstation registered successfully") :
                ResponseEntity.badRequest().body("Workstation registration failed");
    }

    @GetMapping("/select-all")
    public ResponseEntity<List<SimpleWorkstationDTO>> getAll() {
        return ResponseEntity.ok(workstationService.getAllWorkstations());
    }

    @GetMapping("/select-all-by-employee")
    public ResponseEntity<List<SimpleWorkstationDTO>> getAllByEmployee(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workstationService.getAllWorkstationsByEmployee(user));
    }

}

package org.example.backend.controller;

import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
import org.example.backend.service.WorkstationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}

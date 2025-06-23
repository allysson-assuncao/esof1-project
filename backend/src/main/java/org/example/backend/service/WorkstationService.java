package org.example.backend.service;

import org.example.backend.dto.WorkstationRegisterDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Workstation;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.WorkstationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkstationService {
    private WorkstationRepository workstationRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public WorkstationService(WorkstationRepository workstationRepository, CategoryService categoryService) {
        this.workstationRepository = workstationRepository;
        this.categoryRepository = categoryRepository;
    }

    public boolean registerWorkstation(WorkstationRegisterDTO workstationRegisterDTO) {
        if(workstationRegisterDTO == null) return false;

        Workstation workstation = Workstation.builder()
                .name(workstationRegisterDTO.name())
                .categories(new HashSet<>())
                .users(new HashSet<>())
                .ordersQueue(new ArrayList<>())
                .build();

        workstationRepository.save(workstation);

        return true;
    }


}

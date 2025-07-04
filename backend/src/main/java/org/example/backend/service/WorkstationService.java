package org.example.backend.service;

import org.example.backend.dto.Workstation.SimpleWorkstationDTO;
import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
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
    public WorkstationService(WorkstationRepository workstationRepository, CategoryRepository categoryRepository) {
        this.workstationRepository = workstationRepository;
        this.categoryRepository = categoryRepository;
    }

    public boolean registerWorkstation(WorkstationRegisterDTO workstationRegisterDTO) {
        if(workstationRegisterDTO == null) return false;

        Set<Category> categories = new HashSet<>();
        for(UUID it: workstationRegisterDTO.categoryIds()){
            categories.add(categoryRepository.findById(it).orElseThrow());
        }

        Workstation workstation = Workstation.builder()
                .name(workstationRegisterDTO.name())
                .categories(categories)
                .users(new HashSet<>())
                .ordersQueue(new ArrayList<>())
                .build();

        workstationRepository.save(workstation);
        for(Category it: categories){
            it.setWorkstation(workstation);
            categoryRepository.save(it);
        }

        return true;
    }

    public List<SimpleWorkstationDTO> getAllWorkstations() {
        return workstationRepository.findAll()
                .stream()
                .map(SimpleWorkstationDTO::fromEntity)
                .toList();
    }

}

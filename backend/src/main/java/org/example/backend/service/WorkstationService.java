package org.example.backend.service;

import org.example.backend.dto.Workstation.SimpleWorkstationDTO;
import org.example.backend.dto.Workstation.WorkstationRegisterDTO;
import org.example.backend.model.Category;
import org.example.backend.model.User;
import org.example.backend.model.Workstation;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.WorkstationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkstationService {
    private final WorkstationRepository workstationRepository;
    private final CategoryRepository categoryRepository;

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

    @Transactional
    public List<SimpleWorkstationDTO> getAllWorkstationsByEmployee(User user) {
        Set<Workstation> userWorkstations = user.getWorkstations();
        List<Workstation> workstations;

        if (userWorkstations != null && !userWorkstations.isEmpty()) {
            workstations = new ArrayList<>(userWorkstations);
        } else {
            workstations = workstationRepository.findAll();
        }

        return workstations.stream()
                .map(SimpleWorkstationDTO::fromEntity)
                .collect(Collectors.toList());
    }

}

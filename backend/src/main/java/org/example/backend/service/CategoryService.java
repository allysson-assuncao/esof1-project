package org.example.backend.service;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.dto.SimpleCategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Workstation;
import org.example.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.example.backend.repository.WorkstationRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final WorkstationRepository workstationRepository;

    public CategoryService(CategoryRepository categoryRepository, WorkstationRepository workstationRepository) {
        this.categoryRepository = categoryRepository;
        this.workstationRepository = workstationRepository;
    }

    // Método público para criação (falha se já existir)
    @Transactional
    public Category createCategory(CategoryDTO dto) {
        Optional<Category> existing = categoryRepository.findByName(dto.name());
        if (existing.isPresent()) {
            throw new RuntimeException("Categoria já existe com o nome: " + dto.name());
        }
        return saveCategory(dto, null);
    }

    public List<SimpleCategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToSimpleCategoryDTO)
                .collect(Collectors.toList());
    }

    private SimpleCategoryDTO convertToSimpleCategoryDTO(Category category) {
        SimpleCategoryDTO result = new SimpleCategoryDTO(category.getId(), category.getName());
        return result;
    }

    // Método público para atualização
    @Transactional
    public Category updateCategoryById(UUID id, CategoryDTO dto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));

        existingCategory.setMultiple(dto.isMultiple());
        existingCategory.setName(dto.name());

        // Atualiza workstation por ID
        if (dto.workstationId() != null) {
            Workstation workstation = workstationRepository.findById(dto.workstationId())
                    .orElseThrow(() -> new RuntimeException("Workstation não encontrada com ID: " + dto.workstationId()));
            existingCategory.setWorkstation(workstation);
        }

        // Atualiza subcategorias
        if (dto.subcategories() != null) {
            for (CategoryDTO subDTO : dto.subcategories()) {
                if (subDTO == null || subDTO.name() == null) continue;

                Category existingSub = existingCategory.getSubCategories().stream()
                        .filter(cat -> cat.getName().equalsIgnoreCase(subDTO.name()))
                        .findFirst()
                        .orElse(null);

                if (existingSub != null) {
                    if (existingSub.isMultiple() != subDTO.isMultiple()) {
                        existingSub.setMultiple(subDTO.isMultiple());
                    }
                } else {
                    Category newSub = Category.builder()
                            .name(subDTO.name())
                            .isMultiple(subDTO.isMultiple())
                            .parentCategory(existingCategory)
                            .build();
                    existingCategory.getSubCategories().add(newSub);
                }
            }
        }

        return categoryRepository.save(existingCategory);
    }

    // Método auxiliar recursivo para criar categoria e subcategorias
    private Category saveCategory(CategoryDTO dto, Category parent) {
        Category.CategoryBuilder builder = Category.builder()
                .name(dto.name())
                .isMultiple(dto.isMultiple())
                .parentCategory(parent);

        if (dto.workstationId() != null) {
            Workstation workstation = workstationRepository.findById(dto.workstationId())
                    .orElseThrow(() -> new RuntimeException("Workstation não encontrada com ID: " + dto.workstationId()));
            builder.workstation(workstation);
        }

        Category category = builder.build();

        if (dto.subcategories() != null && !dto.subcategories().isEmpty()) {
            Set<Category> subCats = dto.subcategories().stream()
                    .map(subDto -> saveCategory(subDto, category))
                    .collect(Collectors.toSet());
            category.setSubCategories(subCats);
        }

        return categoryRepository.save(category);
    }
}

package org.example.backend.service;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.dto.SimpleCategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Workstation;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.WorkstationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final WorkstationRepository workstationRepository;

    public CategoryService(CategoryRepository categoryRepository, WorkstationRepository workstationRepository) {
        this.categoryRepository = categoryRepository;
        this.workstationRepository = workstationRepository;
    }

    @Transactional
    public Category createCategory(CategoryDTO dto) {
        checkCategoryNameAvailability(dto.name());
        return saveCategory(dto, null);
    }

    public List<SimpleCategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toSimpleCategoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Category updateCategoryById(UUID id, CategoryDTO dto) {
        Category category = findCategoryById(id);

        category.setName(dto.name());
        category.setMultiple(dto.isMultiple());
        updateWorkstation(category, dto.workstationId());
        updateSubcategories(category, dto.subcategories());

        return categoryRepository.save(category);
    }

    /* ----------------------- PRIVATE ----------------------- */

    private void checkCategoryNameAvailability(String name) {
        if (categoryRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Categoria já existe com o nome: " + name);
        }
    }

    private Category findCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));
    }

    private void updateWorkstation(Category category, UUID workstationId) {
        if (workstationId == null) return;

        Workstation workstation = workstationRepository.findById(workstationId)
                .orElseThrow(() -> new RuntimeException("Workstation não encontrada com ID: " + workstationId));
        category.setWorkstation(workstation);
    }

    private void updateSubcategories(Category parentCategory, Set<CategoryDTO> subcategoriesDto) {
        if (subcategoriesDto == null) return;

        for (CategoryDTO subDto : subcategoriesDto) {
            if (subDto == null || subDto.name() == null) continue;

            Category existingSub = parentCategory.getSubCategories().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(subDto.name()))
                    .findFirst()
                    .orElse(null);

            if (existingSub != null) {
                if (existingSub.isMultiple() != subDto.isMultiple()) {
                    existingSub.setMultiple(subDto.isMultiple());
                }
            } else {
                Category newSub = buildCategoryFromDTO(subDto, parentCategory);
                parentCategory.getSubCategories().add(newSub);
            }
        }
    }

    private Category saveCategory(CategoryDTO dto, Category parent) {
        Category category = buildCategoryFromDTO(dto, parent);

        if (dto.subcategories() != null && !dto.subcategories().isEmpty()) {
            Set<Category> subCats = dto.subcategories().stream()
                    .map(subDto -> saveCategory(subDto, category))
                    .collect(Collectors.toSet());
            category.setSubCategories(subCats);
        }

        return categoryRepository.save(category);
    }

    private Category buildCategoryFromDTO(CategoryDTO dto, Category parent) {
        Category.CategoryBuilder builder = Category.builder()
                .name(dto.name())
                .isMultiple(dto.isMultiple())
                .parentCategory(parent);

        if (dto.workstationId() != null) {
            Workstation workstation = workstationRepository.findById(dto.workstationId())
                    .orElseThrow(() -> new RuntimeException("Workstation não encontrada com ID: " + dto.workstationId()));
            builder.workstation(workstation);
        }

        return builder.build();
    }

    private SimpleCategoryDTO toSimpleCategoryDTO(Category category) {
        return new SimpleCategoryDTO(category.getId(), category.getName());
    }
}

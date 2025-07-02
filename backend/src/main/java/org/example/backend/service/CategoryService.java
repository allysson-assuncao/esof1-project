package org.example.backend.service;

import org.example.backend.dto.Category.CategoryDTO;
import org.example.backend.dto.Category.HierarchicalCategoryDTO;
import org.example.backend.dto.Category.SimpleCategoryDTO;
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

    private void updateSubcategories(Category parentCategory, Set<String> subcategoryNames) {
        if (subcategoryNames == null) return;


        Set<CategoryDTO> subcategoriesDto = new HashSet<>();

        for(String it: subcategoryNames) {
            Category tempCategory = categoryRepository.findByName(it)
                    .orElse(Category.builder()
                            .name(it)
                            .parentCategory(parentCategory)
                            .isMultiple(parentCategory.isMultiple())
                            .subCategories(null)
                            .workstation(parentCategory.getWorkstation())
                            .build()
                    );
            System.out.println(tempCategory.getName());
            subcategoriesDto.add(convertToDTO(tempCategory));

        }


        for (CategoryDTO subDto : subcategoriesDto) {
            if (subDto == null || subDto.name() == null) continue;

            Category existingSub = categoryRepository
                    .findByName(subDto.name())
                    .orElse(null);

            if (existingSub != null) {
                existingSub.setMultiple(subDto.isMultiple());
                existingSub.setWorkstation(workstationRepository.findById(subDto.workstationId()).orElse(null));
                existingSub.setParentCategory(parentCategory);
                categoryRepository.save(existingSub);
            } else {
                Category newSub = buildCategoryFromDTO(subDto, parentCategory);
                parentCategory.getSubCategories().add(newSub);
                categoryRepository.save(newSub);
            }
        }
    }

    private Category saveCategory(CategoryDTO dto, Category parent) {
        Category category = buildCategoryFromDTO(dto, parent);

        if (dto.subcategories() != null && !dto.subcategories().isEmpty()) {
            Set<Category> subCats = dto.subcategories().stream()
                    .map(subDto -> saveCategory(
                            convertToDTO(
                                    categoryRepository.findByName(subDto).orElseThrow()), category
                            )
                    )
                    .collect(Collectors.toSet());
            category.setSubCategories(subCats);
        }

        return categoryRepository.save(category);
    }

    private CategoryDTO convertToDTO(Category category){
        UUID workstationId = null;
        if(category.getWorkstation() != null){
             workstationId = category.getWorkstation().getId();
        }

        Set<String> subs = new HashSet<>();

        if(category.getSubCategories() != null){
            subs = category.getSubCategories().stream().map(x -> new String(x.getName())).collect(Collectors.toSet());
        }

        return CategoryDTO.builder()
                .name(category.getName())
                .isMultiple(category.isMultiple())
                .subcategories(subs)
                .workstationId(workstationId)
                .build();
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

    public List<HierarchicalCategoryDTO> getCategoryTree() {
        // 1. Busca todas as categorias que não têm pai (as categorias principais)
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();

        // 2. Mapeia cada categoria raiz para o DTO hierárquico
        return rootCategories.stream()
                .map(this::toHierarchicalCategoryDTO)
                .collect(Collectors.toList());
    }

    private HierarchicalCategoryDTO toHierarchicalCategoryDTO(Category category) {
        // Mapeia recursivamente as subcategorias
        Set<HierarchicalCategoryDTO> subDtos = (category.getSubCategories() == null) ? Collections.emptySet() :
                category.getSubCategories().stream()
                        .map(this::toHierarchicalCategoryDTO)
                        .collect(Collectors.toSet());

        return new HierarchicalCategoryDTO(
                category.getId(),
                category.getName(),
                subDtos
        );
    }
}

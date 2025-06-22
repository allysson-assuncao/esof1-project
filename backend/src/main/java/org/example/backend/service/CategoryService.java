package org.example.backend.service;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category saveOrUpdateCategory(CategoryDTO dto) {
        Category existingCategory = categoryRepository.findByName(dto.name()).orElse(null);

        if (existingCategory == null) {
            return saveCategory(dto, null); // cria normalmente se não existe
        }

        // Atualiza campo 'single' da categoria principal
        existingCategory.setSingle(dto.single());

        if (dto.subcategories() != null && !dto.subcategories().isEmpty()) {
            for (CategoryDTO subDTO : dto.subcategories()) {
                // Procura subcategoria existente pelo nome (ignora maiúsculas/minúsculas)
                Category existingSub = existingCategory.getSubCategories().stream()
                        .filter(cat -> cat.getName().equalsIgnoreCase(subDTO.name()))
                        .findFirst()
                        .orElse(null);

                if (existingSub != null) {
                    // Atualiza o campo 'single' se for diferente
                    if (existingSub.isSingle() != subDTO.single()) {
                        existingSub.setSingle(subDTO.single());
                    }
                } else {
                    // Cria nova subcategoria
                    Category newSub = Category.builder()
                            .name(subDTO.name())
                            .single(subDTO.single())
                            .parentCategory(existingCategory)
                            .build();
                    existingCategory.getSubCategories().add(newSub);
                }
            }
        }

        return categoryRepository.save(existingCategory);
    }

    private Category saveCategory(CategoryDTO dto, Category parent) {
        Category category = Category.builder()
                .name(dto.name())
                .single(dto.single())
                .parentCategory(parent)
                .build();

        // Salva primeiro para gerar ID (se quiser, mas o Cascade ALL já cuida disso)
        // category = categoryRepository.save(category);

        if (dto.subcategories() != null && !dto.subcategories().isEmpty()) {
            Set<Category> subCats = dto.subcategories().stream()
                    .map(subDto -> saveCategory(subDto, category)) // recursivo, passando a categoria atual como pai
                    .collect(Collectors.toSet());
            category.setSubCategories(subCats);
        }

        // Salva categoria (e subcategorias em cascata)
        return categoryRepository.save(category);
    }
}

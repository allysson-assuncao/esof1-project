package org.example.backend.service;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private Category addSubCategoryWithParent(Category parent, String subCategoryName) {
        Category subCategory = categoryRepository.findByName(subCategoryName)
                .orElseGet(() -> {
                    Category newSub = Category.builder().name(subCategoryName).build();
                    return categoryRepository.save(newSub);
                });

        subCategory.setParentCategory(parent);
        return categoryRepository.save(subCategory); // garante persistência com parent atualizado
    }

    // Todo...
    public boolean registerCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.name().isEmpty()) {
            return false;
        }

        // Cria a categoria principal
        Category category = Category.builder()
                .name(categoryDTO.name())
                .build();

        category = categoryRepository.save(category); // salva para gerar ID antes de atribuir como parent

        Set<Category> subCategories = new HashSet<>();

        for (String subCategoryName : categoryDTO.subCategories()) {
            Category subCategory = addSubCategoryWithParent(category, subCategoryName);
            subCategories.add(subCategory);
        }

        category.setSubCategories(subCategories);
        categoryRepository.save(category); // atualiza a lista de subcategorias

        return true;
    }

}

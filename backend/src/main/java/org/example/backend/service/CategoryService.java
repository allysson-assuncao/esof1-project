package org.example.backend.service;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Todo...
    public boolean registerCategory(CategoryDTO categoryDTO) {

        Set<Category> subCategories = null;
        for (String subCategory : categoryDTO.subCategories()) {
            if(!this.categoryRepository.existsByName(subCategory)){
                Category category = Category.builder()
                    .name(categoryDTO.name())
                    .build();
                this.categoryRepository.save(category);
                subCategories.add(category);
            } else {
                subCategories.add(this.categoryRepository.findByName(subCategory).get());
            }
        }
        if(categoryDTO.name().isEmpty()){
            return false;
        }
        Category category = Category.builder()
                .name(categoryDTO.name())
                .subCategories(subCategories)
                .build();

        this.categoryRepository.save(category); // Aqui foi salva a categoria

        for (Category subCategory : subCategories) {
            subCategory.setParentCategory(category);
            this.categoryRepository.save(subCategory); // Aqui foi atualizada todas as subgategorias
        }

        return true;
    }
}

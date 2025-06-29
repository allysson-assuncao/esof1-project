package org.example.backend.controller;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.dto.SimpleCategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/app/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> create(@RequestBody CategoryDTO dto) {
        try {
            categoryService.createCategory(dto);
            return ResponseEntity.status(201).body("Categoria cadastrada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/select-all")
    public ResponseEntity<List<SimpleCategoryDTO>> selectAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody CategoryDTO dto) {
        try {
            categoryService.updateCategoryById(id, dto);
            return ResponseEntity.ok("Categoria atualizada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

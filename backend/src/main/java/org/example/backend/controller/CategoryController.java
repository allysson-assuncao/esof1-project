package org.example.backend.controller;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.dto.SimpleCategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> create(@RequestBody CategoryDTO dto) {
        try {
            Category created = categoryService.createCategory(dto);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/select-all")
    public ResponseEntity<List<SimpleCategoryDTO>> selectAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/{name}")
    public ResponseEntity<?> update(@PathVariable String name, @RequestBody CategoryDTO dto) {
        try {
            Category updated = categoryService.updateCategory(name, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

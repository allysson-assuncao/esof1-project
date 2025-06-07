package org.example.backend.controller;

import org.example.backend.dto.CategoryDTO;
import org.example.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> registerCategory(@RequestBody CategoryDTO categoryDTO) {
        return this.categoryService.registerCategory(categoryDTO) ?
                ResponseEntity.status(HttpStatus.OK).body("Deu certo!") :
                ResponseEntity.badRequest().body("Deu errado");
    }

}

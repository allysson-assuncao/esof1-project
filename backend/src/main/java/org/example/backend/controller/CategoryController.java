package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.backend.dto.Category.CategoryDTO;
import org.example.backend.dto.Category.HierarchicalCategoryDTO;
import org.example.backend.dto.Category.SimpleCategoryDTO;
import org.example.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "create – Endpoint para cadastro de novas categorias")
    public ResponseEntity<?> create(@RequestBody CategoryDTO dto) {
        try {
            CategoryDTO registeredCategory = categoryService.registerOrUpdateCategory(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredCategory);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/select-all")
    @Operation(summary = "selectAll – Exibe todas as categorias da base de dados")
    public ResponseEntity<List<SimpleCategoryDTO>> selectAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "update – Recebe o ID de um categoria e abre body para edição das informações da categoria")
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody CategoryDTO dto) {
        try {
            categoryService.updateCategoryById(id, dto);
            return ResponseEntity.ok("Categoria atualizada com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/structure")
    @Operation(summary = "getCategoryStructure – busca apenas as categorias raiz e, para cada uma, monta a árvore de subcategorias recursivamente")
    public ResponseEntity<List<HierarchicalCategoryDTO>> getCategoryStructure() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }

    @GetMapping("/select-root")
    public List<SimpleCategoryDTO> getRootCategories() {
        return categoryService.getRootCategories();
    }
}

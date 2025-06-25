package org.example.backend.controller;

import org.example.backend.dto.ProductDTO;
import org.example.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/app/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerProduct(@RequestBody ProductDTO productDTO) {
        try {
            productService.registerProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Produto cadastrado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProductByName(@PathVariable UUID id, @RequestBody ProductDTO productDTO) {
        try {
            productService.updateProductById(id, productDTO);
            return ResponseEntity.ok("Produto atualizado com sucesso!");
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msg);
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }
}

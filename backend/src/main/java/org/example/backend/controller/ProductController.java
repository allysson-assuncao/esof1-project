package org.example.backend.controller;

import org.example.backend.dto.ProductDTO;
import org.example.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        boolean success = productService.registerProduct(productDTO);
        if(success) {
            return ResponseEntity.ok("Produto cadastrado com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Erro ao cadastrar produto");
        }
    }
}

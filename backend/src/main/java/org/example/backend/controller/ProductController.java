package org.example.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.backend.dto.Product.ProductRegisterDTO;
import org.example.backend.dto.Product.ProductByCategoryDTO;
import org.example.backend.dto.Product.SimpleProductDTO;
import org.example.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/app/product")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/register")
    @Operation(summary = "registerProduct – Endpoint para cadastro de novos produtos")
    public ResponseEntity<?> registerProduct(@RequestBody ProductRegisterDTO productDTO) {
        try {
            productService.registerProduct(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Produto cadastrado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/select-all")
    @Operation(summary = "selectAll – Exibe todos os produtos da base de dados")
    public ResponseEntity<List<ProductRegisterDTO>> selectAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/select-all-simple")
    @Operation(summary = "selectAllSimple – Exibe todos os produtos da base de dados de forma simplificada, para seleção em pedidos")
    public ResponseEntity<List<SimpleProductDTO>> selectAllSimple() {
        return ResponseEntity.ok(productService.getAllSimpleProducts());
    }


    @PutMapping("/update/{id}")
    @Operation(summary = "updateProductById – Recebe o ID de um produto e abre body para edição das informações do produto")
    public ResponseEntity<?> updateProductById(@PathVariable UUID id, @RequestBody ProductRegisterDTO productDTO) {
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


    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "getProductsByCategory – Recebe o ID de uma categoria e retorna uma lista de todos os produtos")
    public ResponseEntity<?> getProductsByCategory(@PathVariable UUID categoryId) {
        try {
            List<ProductByCategoryDTO> products = productService.getProductsByCategoryId(categoryId);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

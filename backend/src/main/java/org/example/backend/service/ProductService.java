package org.example.backend.service;

import org.example.backend.dto.ProductDTO;
import org.example.backend.dto.SimpleCategoryDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Product;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public void registerProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.name())) {
            throw new RuntimeException("Produto já existe com o nome: " + productDTO.name());
        }

        Category category = categoryRepository.findById(productDTO.idCategory())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + productDTO.idCategory()));

        Product product = Product.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .price(productDTO.price())
                .category(category)
                .active(true)
                .build();

        productRepository.save(product);
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductDTO(
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory().getId() // ou outro campo necessário
                ))
                .collect(Collectors.toList());
    }

    public Product updateProductById(UUID id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        boolean nameChanged = !existingProduct.getName().equals(productDTO.name());
        if (nameChanged && productRepository.existsByName(productDTO.name())) {
            throw new RuntimeException("Já existe outro produto com o nome: " + productDTO.name());
        }

        Category category = categoryRepository.findById(productDTO.idCategory())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + productDTO.idCategory()));

        existingProduct.setName(productDTO.name());
        existingProduct.setDescription(productDTO.description());
        existingProduct.setPrice(productDTO.price());
        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }
}

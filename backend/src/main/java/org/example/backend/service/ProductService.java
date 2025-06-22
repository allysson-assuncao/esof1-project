package org.example.backend.service;

import org.example.backend.dto.ProductDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Product;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Todo...
    public boolean registerProduct(ProductDTO productDTO) {
        if (productRepository.existsByName(productDTO.name())) {
            throw new RuntimeException("Produto já existe com o nome: " + productDTO.name());
        } else {
            if (!productRepository.existsByName(productDTO.name())) {
                try {
                    // Busca categoria pelo nome (pode ser por ID, se preferir)
                    Category category = categoryRepository.findById(productDTO.idCategory())
                            .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + productDTO.idCategory()));

                    Product product = new Product();
                    product.setName(productDTO.name());
                    product.setDescription(productDTO.description());
                    product.setPrice(productDTO.price());
                    product.setCategory(category);
                    product.setActive(true);

                    productRepository.save(product);
                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }

    public Product updateProductById(UUID id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        // Verifica se o nome novo já existe em outro produto
        if (!existingProduct.getName().equals(productDTO.name())
                && productRepository.existsByName(productDTO.name())) {
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

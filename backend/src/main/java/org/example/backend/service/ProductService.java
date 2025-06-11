package org.example.backend.service;

import org.example.backend.dto.ProductDTO;
import org.example.backend.model.Product;
import org.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Todo...
    public boolean registerProduct(ProductDTO productDTO){

        if(!this.productRepository.existsByName(productDTO.name())) {
            try {
                Product product = new Product();
                product.setName(productDTO.name());
                product.setDescription(productDTO.description());
                product.setActive(true);
                product.setPrice(productDTO.price());
                productRepository.save(product);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}

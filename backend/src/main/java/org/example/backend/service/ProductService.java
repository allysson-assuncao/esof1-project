package org.example.backend.service;

import org.example.backend.dto.Product.ProductRegisterDTO;
import org.example.backend.dto.Product.ProductByCategoryDTO;
import org.example.backend.dto.Product.SimpleProductDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Product;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.OrderRepository;
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
    private final OrderRepository orderRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
    }

    public void registerProduct(ProductRegisterDTO productRegisterDTO) {
        if (productRepository.existsByName(productRegisterDTO.name())) {
            throw new RuntimeException("Produto com o nome " + productRegisterDTO.name() + "já existe");
        }

        Category category = categoryRepository.findById(productRegisterDTO.idCategory())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + productRegisterDTO.idCategory()));

        Product product = Product.builder()
                .name(productRegisterDTO.name())
                .description(productRegisterDTO.description())
                .price(productRegisterDTO.price())
                .category(category)
                .active(true)
                .build();

        productRepository.save(product);
    }

    public List<ProductRegisterDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductRegisterDTO(
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory().getId() // ou outro campo necessário
                ))
                .collect(Collectors.toList());
    }

    public Product updateProductById(UUID id, ProductRegisterDTO productRegisterDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        boolean nameChanged = !existingProduct.getName().equals(productRegisterDTO.name());
        if (nameChanged && productRepository.existsByName(productRegisterDTO.name())) {
            throw new RuntimeException("Já existe outro produto com o nome: " + productRegisterDTO.name());
        }

        Category category = categoryRepository.findById(productRegisterDTO.idCategory())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + productRegisterDTO.idCategory()));

        existingProduct.setName(productRegisterDTO.name());
        existingProduct.setDescription(productRegisterDTO.description());
        existingProduct.setPrice(productRegisterDTO.price());
        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);
    }

    public List<ProductByCategoryDTO> getProductsByCategoryId(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoria não encontrada com ID: " + categoryId);
        }

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(product -> new ProductByCategoryDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory().getId()
                ))
                .collect(Collectors.toList());
    }

    public List<SimpleProductDTO> getAllSimpleProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> new SimpleProductDTO(
                        product.getId(),
                        product.getName()
                ))
                .collect(Collectors.toList());
    }

    public List<SimpleProductDTO> selectAllSimpleIfAdditional(Long parentOrderId) {
        List<Product> products;

        if (parentOrderId != null && this.orderRepository.existsById(parentOrderId)) {
            System.out.println("1");
            products = productRepository.findProductsFromParentOrderCategoryAndSubcategories(parentOrderId);
        } else {
            System.out.println("2");
            products = productRepository.findProductsWithCategoryWithSubcategories();
        }

        return products.stream()
                .map(product -> new SimpleProductDTO(product.getId(), product.getName()))
                .collect(Collectors.toList());
    }

}

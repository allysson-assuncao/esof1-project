package org.example.backend.service;

import org.example.backend.dto.Product.ProductRegisterDTO;
import org.example.backend.dto.Product.ProductByCategoryDTO;
import org.example.backend.dto.Product.SimpleProductDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Order;
import org.example.backend.model.Product;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.OrderRepository;
import org.example.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        List<Product> products = new ArrayList<>();

        if (parentOrderId != null && this.orderRepository.existsById(parentOrderId)) {
            System.out.println("DEBUG: Buscando pedido pai " + parentOrderId);
            Optional<Order> parentOrderOpt = orderRepository.findById(parentOrderId);
            if (parentOrderOpt.isPresent()) {
                Order parentOrder = parentOrderOpt.get();
                Product parentProduct = parentOrder.getProduct();
                if (parentProduct == null) {
                    System.out.println("DEBUG: Produto do pedido pai é nulo");
                    return Collections.emptyList();
                }
                Category parentCategory = parentProduct.getCategory();
                if (parentCategory == null) {
                    System.out.println("DEBUG: Categoria do produto do pedido pai é nula");
                    return Collections.emptyList();
                }
                System.out.println("DEBUG: Categoria principal: " + parentCategory.getName() + " (" + parentCategory.getId() + ")");

                // Busca produtos da categoria principal
                List<Product> mainCategoryProducts = productRepository.findByCategoryId(parentCategory.getId());
                System.out.println("DEBUG: Produtos da categoria principal encontrados: " + mainCategoryProducts.size());

                // Busca produtos das subcategorias
                Set<Category> subCategories = parentCategory.getSubCategories();
                List<Product> subCategoryProducts = new ArrayList<>();
                if (subCategories != null && !subCategories.isEmpty()) {
                    List<UUID> subCategoryIds = subCategories.stream().map(Category::getId).collect(Collectors.toList());
                    System.out.println("DEBUG: Subcategorias encontradas: " + subCategoryIds.size());
                    for (UUID subCatId : subCategoryIds) {
                        List<Product> prods = productRepository.findByCategoryId(subCatId);
                        System.out.println("DEBUG: Produtos encontrados para subcategoria " + subCatId + ": " + prods.size());
                        subCategoryProducts.addAll(prods);
                    }
                } else {
                    System.out.println("DEBUG: Nenhuma subcategoria encontrada");
                }

                // Junta e remove duplicados
                Set<Product> allProducts = new HashSet<>();
                /*allProducts.addAll(mainCategoryProducts);*/
                allProducts.addAll(subCategoryProducts);

                System.out.println(allProducts);

                products = new ArrayList<>(allProducts);
            } else {
                System.out.println("DEBUG: Pedido pai não encontrado");
            }
        } else {
            System.out.println("DEBUG: parentOrderId nulo ou pedido não existe, buscando produtos com categoria que tem subcategorias");
            products = productRepository.findProductsWithCategoryWithSubcategories();
        }

        return products.stream()
                .map(product -> new SimpleProductDTO(product.getId(), product.getName()))
                .collect(Collectors.toList());
    }

}

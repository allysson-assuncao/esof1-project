package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.Category.CategorySalesDTO;
import org.example.backend.dto.Category.ProductSalesDTO;
import org.example.backend.dto.Report.MenuReportFilterDTO;
import org.example.backend.model.Category;
import org.example.backend.model.Order;
import org.example.backend.model.Product;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.OrderRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuReportService {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final MenuReportSpecificationService specificationService;

    public List<CategorySalesDTO> getMenuSalesReport(MenuReportFilterDTO filter) {
        Specification<Order> spec = specificationService.getSpecification(filter);
        List<Order> filteredOrders = orderRepository.findAll(spec);

        Map<UUID, ProductSalesDTO> salesByProduct = aggregateSalesData(filteredOrders);

        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();

        return rootCategories.stream()
                .map(category -> buildRecursiveCategorySales(category, salesByProduct))
                .filter(dto -> dto.getQuantitySold() > 0)
                .collect(Collectors.toList());
    }

    private Map<UUID, ProductSalesDTO> aggregateSalesData(List<Order> orders) {
        Map<UUID, ProductSalesDTO> salesMap = new HashMap<>();
        for (Order order : orders) {
            Product product = order.getProduct();
            ProductSalesDTO current = salesMap.get(product.getId());
            long newQuantity = (current != null ? current.quantitySold() : 0L) + order.getAmount();
            BigDecimal newTotal = (current != null ? current.totalValue() : BigDecimal.ZERO)
                    .add(BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(order.getAmount())));

            ProductSalesDTO updated = ProductSalesDTO.builder()
                    .productId(product.getId())
                    .name(product.getName())
                    .unitPrice(product.getPrice())
                    .quantitySold(newQuantity)
                    .totalValue(newTotal)
                    .build();

            salesMap.put(product.getId(), updated);
        }
        return salesMap;
    }

    private CategorySalesDTO buildRecursiveCategorySales(Category category, Map<UUID, ProductSalesDTO> salesByProduct) {
        List<ProductSalesDTO> productSales = category.getProducts().stream()
                .filter(product -> salesByProduct.containsKey(product.getId()))
                .map(product -> salesByProduct.get(product.getId()))
                .collect(Collectors.toList());

        List<CategorySalesDTO> subCategorySales = category.getSubCategories().stream()
                .map(subCategory -> buildRecursiveCategorySales(subCategory, salesByProduct))
                .filter(dto -> dto.getQuantitySold() > 0)
                .collect(Collectors.toList());

        long totalQuantity = productSales.stream().mapToLong(ProductSalesDTO::quantitySold).sum()
                + subCategorySales.stream().mapToLong(CategorySalesDTO::getQuantitySold).sum();

        BigDecimal totalValue = productSales.stream().map(ProductSalesDTO::totalValue).reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(subCategorySales.stream().map(CategorySalesDTO::getTotalValue).reduce(BigDecimal.ZERO, BigDecimal::add));

        return CategorySalesDTO.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .quantitySold(totalQuantity)
                .totalValue(totalValue)
                .productSales(productSales)
                .subCategorySales(subCategorySales)
                .build();
    }
}

package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // enables logging
public class MenuReportService {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final MenuReportSpecificationService specificationService;

    public List<CategorySalesDTO> getMenuSalesReport(MenuReportFilterDTO filter) {
        MenuReportFilterDTO effectiveFilter = filter;

        if (filter.categoryIds() != null && !filter.categoryIds().isEmpty()) {
            log.info("Finding all descendant categories for IDs: {}", filter.categoryIds());
            Set<UUID> allCategoryIds = findAllCategoryAndDescendantIds(filter.categoryIds());
            log.info("Total categories to filter (including descendants): {}", allCategoryIds.size());

            effectiveFilter = new MenuReportFilterDTO(
                    filter.startDate(),
                    filter.endDate(),
                    filter.businessDayStartTime(),
                    allCategoryIds,
                    filter.productIds(),
                    null,
                    null
            );
        }

        Specification<Order> spec = specificationService.getSpecification(effectiveFilter);
        List<Order> ordersFromDb = orderRepository.findAll(spec);

        Map<UUID, ProductSalesDTO> salesByProduct = aggregateSalesData(ordersFromDb);

        Map<UUID, ProductSalesDTO> filteredSalesByProduct;

        if (filter.minPrice() != null || filter.maxPrice() != null) {
            filteredSalesByProduct = salesByProduct.entrySet().stream()
                    .filter(entry -> {
                        ProductSalesDTO productSales = entry.getValue();
                        BigDecimal totalSalesValue = productSales.totalValue();

                        boolean minPriceOk = (filter.minPrice() == null) ||
                                (totalSalesValue.compareTo(BigDecimal.valueOf(filter.minPrice())) >= 0);

                        boolean maxPriceOk = (filter.maxPrice() == null) ||
                                (totalSalesValue.compareTo(BigDecimal.valueOf(filter.maxPrice())) <= 0);

                        return minPriceOk && maxPriceOk;
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            filteredSalesByProduct = salesByProduct;
        }

        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();

        return rootCategories.stream()
                .map(category -> buildRecursiveCategorySales(category, filteredSalesByProduct))
                .filter(dto -> dto.getQuantitySold() > 0)
                .collect(Collectors.toList());
    }

    private Set<UUID> findAllCategoryAndDescendantIds(Set<UUID> initialCategoryIds) {
        Set<UUID> allIds = new HashSet<>(initialCategoryIds);
        Queue<UUID> queue = new LinkedList<>(initialCategoryIds);

        while (!queue.isEmpty()) {
            Set<UUID> currentBatch = new HashSet<>();
            while (!queue.isEmpty()) {
                currentBatch.add(queue.poll());
            }

            if (currentBatch.isEmpty()) continue;

            Set<Category> children = categoryRepository.findByParentCategoryIds(currentBatch);

            for (Category child : children) {
                if (allIds.add(child.getId())) {
                    queue.add(child.getId());
                }
            }
        }
        return allIds;
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
                    .active(product.isActive())
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

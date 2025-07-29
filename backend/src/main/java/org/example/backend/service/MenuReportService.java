package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.Category.CategorySalesDTO;
import org.example.backend.dto.Category.ProductSalesDTO;
import org.example.backend.dto.Report.MenuPerformanceMetricsDTO;
import org.example.backend.dto.Report.MenuReportFilterDTO;
import org.example.backend.dto.Report.ProductSalesProjection;
import org.example.backend.model.Category;
import org.example.backend.model.Order;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.OrderRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuReportService {

    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final MenuReportSpecificationService specificationService;

    public List<CategorySalesDTO> getMenuSalesReport(MenuReportFilterDTO filter) {
        LocalTime businessDayStart = Optional.ofNullable(filter.businessDayStartTime())
                .orElse(LocalTime.of(18, 0));
        LocalTime businessDayEnd = LocalTime.of(2, 0);

        LocalDateTime queryStartDate = null;
        if (filter.startDate() != null) {
            queryStartDate = filter.startDate().toLocalDate().atTime(businessDayStart);
        }
        LocalDateTime queryEndDate = null;
        if (filter.endDate() != null) {
            queryEndDate = filter.endDate().toLocalDate().atTime(businessDayEnd);
            if (businessDayEnd.isBefore(businessDayStart)) {
                queryEndDate = queryEndDate.plusDays(1);
            }
        }

        Set<UUID> productUuids = (filter.productIds() == null || filter.productIds().isEmpty())
                ? Collections.emptySet()
                : filter.productIds();

        List<UUID> allCategoryUuids = (filter.categoryIds() == null || filter.categoryIds().isEmpty())
                ? Collections.emptyList()
                : new ArrayList<>(findAllCategoryAndDescendantIds(filter.categoryIds()));

        List<ProductSalesProjection> salesProjections = orderRepository.getAggregatedMenuSales(
                queryStartDate,
                queryEndDate,
                productUuids,
                allCategoryUuids,
                filter.minPrice() == null ? null : BigDecimal.valueOf(filter.minPrice()),
                filter.maxPrice() == null ? null : BigDecimal.valueOf(filter.maxPrice())
        );

        Map<UUID, ProductSalesDTO> salesByProduct = salesProjections.stream()
                .map(p -> new ProductSalesDTO(
                        p.getProductId(), p.getName(), p.getUnitPrice(), p.getActive(),
                        p.getQuantitySold(), p.getTotalValue()
                ))
                .collect(Collectors.toMap(ProductSalesDTO::productId, Function.identity()));

        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return rootCategories.stream()
                .map(category -> buildRecursiveCategorySales(category, salesByProduct))
                .filter(dto -> dto.getQuantitySold() > 0)
                .collect(Collectors.toList());
    }

    public MenuPerformanceMetricsDTO getMenuPerformanceMetrics(MenuReportFilterDTO filter) {
        MenuReportFilterDTO effectiveFilter = applyDescendantCategoryFilter(filter);

        Specification<Order> spec = specificationService.getSpecification(effectiveFilter);
        List<Order> ordersFromDb = orderRepository.findAll(spec);

        if (ordersFromDb.isEmpty()) {
            return MenuPerformanceMetricsDTO.builder()
                    .totalRevenue(BigDecimal.ZERO)
                    .totalItemsSold(0L)
                    .uniqueProductsSold(0L)
                    .build();
        }

        BigDecimal totalRevenue = ordersFromDb.stream()
                .map(order -> BigDecimal.valueOf(order.getProduct().getPrice()).multiply(BigDecimal.valueOf(order.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalItemsSold = ordersFromDb.stream()
                .mapToLong(Order::getAmount)
                .sum();

        long uniqueProductsSold = ordersFromDb.stream()
                .map(order -> order.getProduct().getId())
                .distinct()
                .count();

        return MenuPerformanceMetricsDTO.builder()
                .totalRevenue(totalRevenue)
                .totalItemsSold(totalItemsSold)
                .uniqueProductsSold(uniqueProductsSold)
                .build();
    }

    private MenuReportFilterDTO applyDescendantCategoryFilter(MenuReportFilterDTO filter) {
        if (filter.categoryIds() != null && !filter.categoryIds().isEmpty()) {
            Set<UUID> allCategoryIds = findAllCategoryAndDescendantIds(filter.categoryIds());
            return new MenuReportFilterDTO(
                    filter.startDate(), filter.endDate(), filter.businessDayStartTime(),
                    allCategoryIds, filter.productIds(), filter.minPrice(), filter.maxPrice()
            );
        }
        return filter;
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

    /*private Map<UUID, ProductSalesDTO> aggregateSalesData(List<Order> orders) {
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
    }*/

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

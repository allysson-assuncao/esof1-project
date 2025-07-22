package org.example.backend.dto.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategorySalesDTO {
    private UUID categoryId;
    private String name;
    private long quantitySold;
    private BigDecimal totalValue;

    @Builder.Default
    private List<CategorySalesDTO> subCategorySales = new ArrayList<>();

    @Builder.Default
    private List<ProductSalesDTO> productSales = new ArrayList<>();
}

package org.example.backend.dto.Report;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProductSalesProjection {
    UUID getProductId();

    String getName();

    BigDecimal getUnitPrice();

    Boolean getActive();

    Long getQuantitySold();

    BigDecimal getTotalValue();

    UUID getCategoryId();
}

package com.hfsolution.feature.stockmanagement.dto.request.stock;


import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockRequest {

    @Positive(message = "Product ID must be greater than 0")
    private Long productId;
    @Positive(message = "Qty must be greater than 0")
    private Long qty;
    
}

package com.hfsolution.feature.stockmanagement.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockRequest {

    private Long productId;
    @Positive(message = "Qty must be greater than 0")
    private Long qty;
    
}

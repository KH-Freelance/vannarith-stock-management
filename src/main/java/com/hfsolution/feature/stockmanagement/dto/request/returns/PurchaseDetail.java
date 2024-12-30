package com.hfsolution.feature.stockmanagement.dto.request.returns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseDetail {

    @NotBlank(message = "Batch ID is required.")
    private String batchId;  

    @Positive(message = "Product ID must numeric.")
    private Long productId;  
}
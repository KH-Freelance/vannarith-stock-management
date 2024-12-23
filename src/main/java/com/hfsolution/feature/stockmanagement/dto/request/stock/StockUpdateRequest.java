package com.hfsolution.feature.stockmanagement.dto.request.stock;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockUpdateRequest {

    @Positive(message = "Qty must be greater than 0")
    private Long qty;
    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}$",
        message = "Expiry date must be in the format yyyy-MM-dd."
    )
    private String expiryDate;
    private String remark;
    
}

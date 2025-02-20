package com.hfsolution.feature.stockmanagement.dto.request.stock;


import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AdjustQty {

    @Positive(message = "Qty must be greater than 0")
    private Long qty;

    private String remark;
    
}

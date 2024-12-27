package com.hfsolution.feature.stockmanagement.dto.request.expense;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ExpenseRequest {

    @NotBlank(message = "Remark is required.")
    private String remark;
    @Positive(message = "Price must be greater than 0")
    private BigDecimal amount;

}
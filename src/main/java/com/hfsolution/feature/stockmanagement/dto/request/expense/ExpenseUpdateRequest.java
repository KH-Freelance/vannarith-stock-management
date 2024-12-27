package com.hfsolution.feature.stockmanagement.dto.request.expense;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpenseUpdateRequest {

    @NotBlank(message = "Remark is required.")
    private String remark;

    @Positive(message = "Price must be greater than 0")
    private BigDecimal amount;
    
}
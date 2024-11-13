package com.hfsolution.feature.stockmanagement.dto.request.purchase;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PayRequest {
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
}
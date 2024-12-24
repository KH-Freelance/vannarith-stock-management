package com.hfsolution.feature.stockmanagement.dto.request.purchase;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PayRequest {
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotBlank(message = "Payment Type is required.")
    @Pattern(regexp = "^(ON_HAND|BANK)$", 
             message = "Type not match , please check and try again !")
    private String type;
}
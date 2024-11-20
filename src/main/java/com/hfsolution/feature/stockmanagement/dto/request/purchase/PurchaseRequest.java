package com.hfsolution.feature.stockmanagement.dto.request.purchase;

import java.math.BigDecimal;

import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseRequest {

    @Positive(message = "Product ID must be greater than 0")
    private Long productId;
    @Positive(message = "Customer ID must be greater than 0")
    private Long customerId;
    @Positive(message = "Qty must be greater than 0")
    private Long qty;
    @Positive(message = "discount must be greater than 0")
    private BigDecimal discount;
    private String location;
    @NotNull(message = "Payment type cannot be null")
    private PaymentType paymentType;
    
}

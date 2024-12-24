package com.hfsolution.feature.stockmanagement.dto.request.purchase;

import java.math.BigDecimal;
import java.util.List;

import com.hfsolution.feature.stockmanagement.enums.PaymentType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseRequest {

    @NotEmpty(message = "Product purchases cannot be empty")
    @Valid
    private List<ProductPurchase> productPurchases;
    @Positive(message = "Customer ID must be greater than 0")
    private Long customerId;
    // @Positive(message = "discount must be greater than 0")
    private BigDecimal discount;
    private String location;
    @NotNull(message = "Payment type cannot be null")
    private PaymentType paymentType;
    @Pattern(regexp = "^(ON_HAND|BANK)$", 
             message = "Payment Method not match , please check and try again !")
    private String paymentMethod;

    

    @Data
    public static class ProductPurchase {
        @NotBlank(message = "Batch Id is required.")
        private String batchId;
        @Positive(message = "Product ID must be greater than 0")
        private Long productId;
        @Positive(message = "Qty must be greater than 0")
        private Long qty;
    }
    
}

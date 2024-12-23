package com.hfsolution.feature.stockmanagement.dto.request.purchase;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseUpdateRequest {

    @Positive(message = "Product ID must be greater than 0")
    private Long productId;
    @Positive(message = "Customer ID must be greater than 0")
    private Long customerId;
    @Positive(message = "User ID must be greater than 0")
    private Long userId; 
    @Positive(message = "Qty must be greater than 0")
    private Long qty;
    private String location;
    @Pattern(regexp = "^(CASH|BANK|ONE_WEEK|TWO_WEEK|THREE_WEEK|ONE_MONTH|TWO_MONTH|THREE_MONTH|FIVE_MONTH|SIX_MONTH)$", 
             message = "Payment type not match , please check and try again !")
    private String paymentType;
    
}
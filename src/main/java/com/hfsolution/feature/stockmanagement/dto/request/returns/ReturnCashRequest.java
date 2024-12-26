package com.hfsolution.feature.stockmanagement.dto.request.returns;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ReturnCashRequest {

    
    @NotBlank(message = "Purchase code is required.")
    private String purchaseCode;  

    @NotNull(message = "Product Id purchases cannot be empty")
    @Valid
    private List<Long> productIds;    
}
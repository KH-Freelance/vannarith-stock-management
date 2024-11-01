package com.hfsolution.feature.stockmanagement.dto.request.customer;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "CustomerName is required.")
    private String customerName;
    private String email;
    @NotBlank(message = "phone is required.")
    private String phone;
    private String address;
    private BigDecimal discount;

}
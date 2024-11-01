package com.hfsolution.feature.stockmanagement.dto.request.customer;

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
public class CustomerUpdateRequest {

    @NotBlank(message = "CustomerName is required.")
    private String customerName;
    private String email;
    @NotBlank(message = "phone is required.")
    private String phone;
    private String address;
    // @Positive(message = "credit must be greater than 0")
    // @NotBlank(message = "credit is required.")
    private BigDecimal credit;
    private BigDecimal discount;
    
}
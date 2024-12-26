package com.hfsolution.feature.stockmanagement.dto.request.product;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProductUpdateRequest {
    private String productName;
    private String productDesc;
    private BigDecimal price;
    //private String factory;
    private BigDecimal importPrice;
    private MultipartFile file;
    // @Pattern(
    //     regexp = "^\\d{4}-\\d{2}-\\d{2}$",
    //     message = "Expiry date must be in the format yyyy-MM-dd."
    // )
    // private String expiryDate;
}
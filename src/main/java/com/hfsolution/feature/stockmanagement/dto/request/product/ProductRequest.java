package com.hfsolution.feature.stockmanagement.dto.request.product;


import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required.")
    private String productName;
    private String productDesc;
    private String packSize;
    // @NotBlank(message = "Factory name is required.")
    //private String factory;
    private MultipartFile file;
    @NotBlank(message = "Product name is required.")
    private String customId;
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price; 
    private BigDecimal discount;
    // @NotBlank(message = "Expiry date is required.")
    // @Pattern(
    //     regexp = "^\\d{4}-\\d{2}-\\d{2}$",
    //     message = "Expiry date must be in the format yyyy-MM-dd."
    // )
    // private String expiryDate;
    // @Pattern(
    // regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$",
    // message = "Expiry datetime must be in the format yyyy-MM-dd HH:mm."
    // )
    // private String expiryDate;
    
}

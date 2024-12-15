package com.hfsolution.feature.stockmanagement.dto.product;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductDto {


    private Long id;


    private String productName;


    private String productDesc;


    private BigDecimal price;

    private String factory;

    private BigDecimal importPrice;

    private BigDecimal discount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Phnom_Penh")
    private Timestamp expiryDate;

    private String imageUrl;
  
}

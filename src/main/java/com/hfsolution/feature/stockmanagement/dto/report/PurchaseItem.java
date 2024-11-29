package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.hfsolution.feature.stockmanagement.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PurchaseItem {
    
    private Long id;

    private String productName;

    private String productDesc;
    
    private String factory;
    
    private Long qty;
    
    private BigDecimal price;
    
    private BigDecimal importPrice;
    
    private BigDecimal discount;
    
    private Timestamp createdDate;
    
    private Timestamp expiryDate;
}


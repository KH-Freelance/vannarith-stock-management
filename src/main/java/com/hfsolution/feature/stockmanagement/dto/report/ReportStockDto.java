package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportStockDto {

    private Long productId;

    private Long stockId;
    
    private String productName;
    
    private BigDecimal salePrice;

    private Double totalAsset;

    private String factory;

    private BigDecimal importPrice;

    private long stockOnHand;

    private long stockSold;
    
    private BigDecimal discount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp expiryDate;

    
}

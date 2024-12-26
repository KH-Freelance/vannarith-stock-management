package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.app.util.TimestampConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportStockDto {


    @ExcelIgnore
    private Long productId;

    @ExcelIgnore
    private Long stockId;

    @ExcelProperty("Batch ID")
    private String batchId;
    
    @ExcelProperty("Product Name")
    private String productName;
    
    @ExcelProperty("Sale Price")
    private BigDecimal salePrice;

    @ExcelProperty("Asset Value")
    private BigDecimal assetValue;

    @ExcelProperty("Retail Value")
    private BigDecimal retailValue;

    @ExcelProperty("% of Total Asset")
    private Double totalAsset;

    @ExcelIgnore
    private String factory;

    @ExcelProperty("Avg Cost")
    private BigDecimal avgCost;

    @ExcelProperty("On Hand")
    private long stockOnHand;
    
    @ExcelIgnore
    private BigDecimal discount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @ExcelProperty(value = "Created Date", converter = TimestampConverter.class)
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @ExcelProperty(value = "Expire Date", converter = TimestampConverter.class)
    private Timestamp expiryDate;

    
}

package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hfsolution.app.util.TimestampConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomReturnDto {
    
    @ExcelProperty("Purchase Code")
    private String purchaseCode;

    @ExcelProperty("Product Name")
    private String productName;

    @ExcelProperty("Product's Batch Number")
    private String batchId;

    @ExcelProperty("Refund Amount")
    private BigDecimal refundAmount;
    
    @ExcelProperty("Customer")
    private String customer;
    
    @ExcelProperty("Type")
    private String type;
    
    @ExcelProperty("Returned To Sales")
    private String returnedToSales;
    
    @ExcelProperty("Returned By")
    private String returnedBy;
    
    @ExcelProperty(value = "Returned At",converter = TimestampConverter.class)
    private Timestamp returnedAt;
}

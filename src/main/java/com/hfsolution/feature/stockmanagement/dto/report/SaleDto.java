package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hfsolution.app.util.TimestampConverter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleDto {

    @ExcelProperty("Type")
    @JsonProperty("Type")
    private String type;

    @ExcelProperty(value = "Date", converter = TimestampConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonProperty("Date")
    private Timestamp createdDate;

    @ExcelProperty("Purchase Code")
    @JsonProperty("Purchase Code")
    private String purchaseCode;

    @ExcelProperty("Customer")
    @JsonProperty("Customer")
    private String customerName;
    

    @ExcelProperty("Product")
    @JsonProperty("Product")
    private String productName;

    @ExcelProperty("INN")
    @JsonProperty("INN")
    private String productDesc;
    
    @ExcelProperty("Phone")
    @JsonProperty("Phone")
    private String customerPhone;

    @ExcelProperty("Location")
    @JsonProperty("Location")
    private String location;

    @ExcelProperty("Sales Price")
    @JsonProperty("Sales Price")
    private BigDecimal salePrice;

    @ExcelProperty("Qty")
    @JsonProperty("Qty")
    private long qty;
    
    @ExcelProperty("Total Amount")
    @JsonProperty("Total Amount")
    private BigDecimal totalAmount;

 

}

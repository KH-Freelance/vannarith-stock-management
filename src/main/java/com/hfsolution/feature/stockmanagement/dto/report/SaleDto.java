package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.app.util.TimestampConverter;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleDto {

    @ExcelProperty("Type")
    private String type;

    @ExcelProperty(value = "Created Date", converter = TimestampConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;

    @ExcelProperty("Purchase Code")
    private String purchaseCode;

    @ExcelProperty("Customer Name")
    private String customerName;
    

    @ExcelProperty("Product Name")
    private String productName;

    @ExcelProperty("INN")
    private String productDesc;
    
    @ExcelProperty("Customer Phone")
    private String customerPhone;

    @ExcelProperty("Location")
    private String location;

    @ExcelProperty("Sale Price")
    private BigDecimal salePrice;

    @ExcelProperty("Qty")
    private long qty;
    
    @ExcelProperty("Total Amount")
    private BigDecimal totalAmount;

 

}

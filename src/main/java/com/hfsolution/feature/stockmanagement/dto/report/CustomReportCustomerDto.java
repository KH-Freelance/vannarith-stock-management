package com.hfsolution.feature.stockmanagement.dto.report;


import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomReportCustomerDto {

    @ExcelIgnore
    private Long customerId;
    @ExcelProperty("Customer")
    private String customerName;
    @ExcelProperty("Credit")
    private BigDecimal currentCredit;
    @ExcelProperty("1 - 30")
    private BigDecimal creditDay1To30 = BigDecimal.ZERO;
    @ExcelProperty("31 - 60")
    private BigDecimal creditDay31To60 = BigDecimal.ZERO;
    @ExcelProperty("61 - 90")
    private BigDecimal creditDay61To90 = BigDecimal.ZERO;
    @ExcelProperty(" > 90")
    private BigDecimal creditMoreThan90 = BigDecimal.ZERO;
    @ExcelProperty("Total Credit")
    private BigDecimal totalCredit;
    
}

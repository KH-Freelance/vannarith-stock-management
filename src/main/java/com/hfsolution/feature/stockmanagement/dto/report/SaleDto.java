package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaleDto {

    private String purchaseCode;


    private String type;

    private String customerName;
    
    private String productName;

    private String productDesc;
    
    private String customerPhone;
    

    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    // private Timestamp expiryDate;

    private String location;

    private long qty;
    
    private BigDecimal salePrice;
    
    private BigDecimal totalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;

}

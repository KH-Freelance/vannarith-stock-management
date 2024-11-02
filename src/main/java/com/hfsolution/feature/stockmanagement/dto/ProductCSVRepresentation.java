package com.hfsolution.feature.stockmanagement.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.opencsv.bean.CsvBindByName;

public class ProductCSVRepresentation {

    @CsvBindByName(column = "id")
    private Long id;

    @CsvBindByName(column = "product_name")
    private String productName;

    @CsvBindByName(column = "product_desc")
    private String productDesc;

    @CsvBindByName(column = "price")
    private BigDecimal price;

    @CsvBindByName(column = "discount")
    private BigDecimal discount;
    
    @CsvBindByName(column = "created_date")
    private Timestamp createdDate;

    @CsvBindByName(column = "updated_date")
    private Timestamp updatedDate;

    @CsvBindByName(column = "expiry_date")
    private Timestamp expiryDate;
  
}

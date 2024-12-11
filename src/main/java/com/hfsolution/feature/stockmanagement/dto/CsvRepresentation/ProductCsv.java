package com.hfsolution.feature.stockmanagement.dto.CsvRepresentation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private String productName;

    @CsvBindByPosition(position = 2)
    private String productDesc;

    @CsvBindByPosition(position = 3)
    private BigDecimal price;

    @CsvBindByPosition(position = 4)
    private String factory;

    @CsvBindByPosition(position = 5)
    private BigDecimal importPrice;

    @CsvBindByPosition(position = 6)
    private BigDecimal discount;
    
    @CsvDate(value = "yyyy-MM-dd hh:mm:ss")
    @CsvBindByPosition(position = 7)
    private Timestamp createdDate;

    
    @CsvDate(value = "yyyy-MM-dd hh:mm:ss")
    @CsvBindByPosition(position = 8)
    private Timestamp updatedDate;

    @CsvBindByPosition(position = 9)
    @CsvDate(value = "yyyy-MM-dd")
    private Timestamp expiryDate;

    @CsvBindByPosition(position = 10)
    private String imageUrl;
  
}

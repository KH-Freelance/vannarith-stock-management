package com.hfsolution.feature.stockmanagement.dto.CsvRepresentation;


import com.opencsv.bean.CsvBindByPosition;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private Long productId;

    @CsvBindByPosition(position = 2)
    private String productName;

    @CsvBindByPosition(position = 3)
    private Long qty;

    @CsvBindByPosition(position = 4)
    private Double percentage;

    @CsvBindByPosition(position = 5)
    private String createdDate;

    @CsvBindByPosition(position = 6)
    private String updatedDate;


}
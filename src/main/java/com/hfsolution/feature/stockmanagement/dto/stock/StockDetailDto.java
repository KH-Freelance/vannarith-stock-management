package com.hfsolution.feature.stockmanagement.dto.stock;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDetailDto {

    private Long id;

    private ProductDto product;

    private Long qty;

    private List<StockHistoryDto> stockHistories = new ArrayList<>();

    private Double percentage;

    private Timestamp createdDate;

    private Timestamp updatedDate;

}

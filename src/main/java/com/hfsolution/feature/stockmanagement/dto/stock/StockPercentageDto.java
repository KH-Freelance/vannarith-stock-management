package com.hfsolution.feature.stockmanagement.dto.stock;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StockPercentageDto {
    private Double percentage; // corresponds to SUM(total)
    private Long qty; // corresponds to SUM(total)
    private Long id;

    // Constructor
    public StockPercentageDto(Long id, Long qty, Double percentage) {
       this.id = id;
       this.qty = qty;
       this.percentage = percentage;
    }

}

package com.hfsolution.feature.stockmanagement.dto.response.stock;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockDto {

    private Long id;
    private String productName;
    private long productId;
    private Product product;
    private Long qty;
    private Double percentage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp updatedDate;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Product {
        long id;
        String name;
    }
}

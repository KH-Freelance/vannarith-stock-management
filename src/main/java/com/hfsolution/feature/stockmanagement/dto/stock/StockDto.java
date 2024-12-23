package com.hfsolution.feature.stockmanagement.dto.stock;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockDto {

    private Long id;
    private String batchId;
    private Product product;
    private Long qty;
    private Double percentage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Phnom_Penh")
    private Timestamp expiryDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Product {
        long id;
        String productName;
    }
}

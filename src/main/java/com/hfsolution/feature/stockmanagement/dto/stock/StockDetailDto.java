package com.hfsolution.feature.stockmanagement.dto.stock;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDetailDto {

    private Long id;

    private String batchId;

    private ProductDto product;

    private Long qty;

    private List<StockHistoryDto> stockHistories = new ArrayList<>();

    private Double percentage;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Phnom_Penh")
    private Timestamp expiryDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

}

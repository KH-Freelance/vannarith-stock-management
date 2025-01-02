package com.hfsolution.feature.stockmanagement.dto.purchase;
import lombok.Data;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;


@Data
public class PurchaseItemDto {


    private Long id;

    private String batchId;

    private ProductDto product;

    private String status;

    private Long qty;

    // @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal price;

    // @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal discount;

   


}

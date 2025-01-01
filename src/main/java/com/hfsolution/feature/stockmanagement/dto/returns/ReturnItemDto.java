package com.hfsolution.feature.stockmanagement.dto.returns;
import lombok.Data;
import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;


@Data
public class ReturnItemDto {


    private Long id;

    private String batchId;

    private ProductDto product;

    private Long qty;

    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal price;



}

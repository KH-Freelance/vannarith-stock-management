package com.hfsolution.feature.stockmanagement.dto.purchase;
import lombok.Data;
import java.math.BigDecimal;
import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;


@Data
public class PurchaseItemDto {


    private Long id;

    private String batchId;

    private ProductDto product;

    private String status;

    private Long qty;

    private BigDecimal price;

    private BigDecimal discount;

   


}

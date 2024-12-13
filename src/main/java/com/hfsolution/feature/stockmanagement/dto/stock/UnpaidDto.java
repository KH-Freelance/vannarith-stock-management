package com.hfsolution.feature.stockmanagement.dto.stock;

import java.util.List;

import com.hfsolution.feature.stockmanagement.dto.purchase.PurchaseDetailDto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnpaidDto {
    List<PurchaseDetailDto> content;
}

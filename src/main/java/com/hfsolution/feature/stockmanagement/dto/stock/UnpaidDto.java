package com.hfsolution.feature.stockmanagement.dto.stock;

import java.util.List;

import com.hfsolution.feature.stockmanagement.entity.Purchase;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UnpaidDto {
    List<Purchase> content;
}

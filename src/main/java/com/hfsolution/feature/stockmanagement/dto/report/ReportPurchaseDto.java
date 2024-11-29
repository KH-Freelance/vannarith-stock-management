package com.hfsolution.feature.stockmanagement.dto.report;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportPurchaseDto {

    BigDecimal totalCostSold;
    long totalProductSoldCount;
    List<PurchaseDto> purchaseOrders;
    
}

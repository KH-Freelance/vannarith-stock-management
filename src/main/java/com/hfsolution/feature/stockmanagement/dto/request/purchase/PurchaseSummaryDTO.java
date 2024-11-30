package com.hfsolution.feature.stockmanagement.dto.request.purchase;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PurchaseSummaryDTO {
    private String createdDate; // corresponds to TO_CHAR(created_date, 'YYYY-MM')
    private BigDecimal totalAmountSum; // corresponds to SUM(total)
    private String customerName; // corresponds to SUM(total)
    private Long customerId;

    // Constructor
    public PurchaseSummaryDTO(String createdDate, BigDecimal totalAmountSum, long customerId, String customerName) {
        this.createdDate = createdDate;
        this.totalAmountSum = totalAmountSum;
        this.customerId = customerId;
        this.customerName = customerName;
    }

}

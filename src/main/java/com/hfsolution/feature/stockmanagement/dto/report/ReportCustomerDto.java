package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportCustomerDto {
    private Long customerId;
    private String customerName;
    private CreditCategories creditCategories;
    private BigDecimal currentCredit;
    private BigDecimal totalCredit;

    @Setter
    @Getter
    public static class CreditCategories {
        private BigDecimal creditDay1To30 = BigDecimal.ZERO;
        private BigDecimal creditDay31To60 = BigDecimal.ZERO;
        private BigDecimal creditDay61To90 = BigDecimal.ZERO;
        private BigDecimal creditMoreThan90 = BigDecimal.ZERO;
    }
}

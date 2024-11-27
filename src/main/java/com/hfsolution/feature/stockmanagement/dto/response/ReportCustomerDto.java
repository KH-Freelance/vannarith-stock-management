package com.hfsolution.feature.stockmanagement.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReportCustomerDto {
    private Long customerId;
    private String customerName;
    private CreditCategoies creditCategoies;
    private BigDecimal currentCredit;
    private BigDecimal totalCredit;

    @Setter
    @Getter
    public static class CreditCategoies {
        private BigDecimal creditDay1To30 = BigDecimal.ZERO;
        private BigDecimal creditDay31To60 = BigDecimal.ZERO;
        private BigDecimal creditDay61To90 = BigDecimal.ZERO;
        private BigDecimal creditMoreThan90 = BigDecimal.ZERO;
    }
}

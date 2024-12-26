package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PaymentSummary {
   
    private Long id;

    private BigDecimal amount;

    private Timestamp createdDate;

    private Timestamp updateDate;
}

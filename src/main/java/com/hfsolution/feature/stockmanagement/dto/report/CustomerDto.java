package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto {
  
    private Long id;

    private String customerName;

    private String email;

    private String phone;

    private String address;

    private BigDecimal discount;

    private BigDecimal credit;

    private Timestamp createdDate;

    private Timestamp updatedDate;
}

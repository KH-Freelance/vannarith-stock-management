package com.hfsolution.feature.stockmanagement.dto.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PurchaseDto {
    
    private Long id;

    private List<PurchaseItem> purchaseItems = new ArrayList<>();

    private CustomerDto customer; 

    private List<PaymentSummary> transactionSummaries = new ArrayList<>();

    private Long qty;

    private BigDecimal total;

    private PaymentType paymentType = PaymentType.CASH;

    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    private String location;

    private String purchaseCode;

    private Timestamp createdDate;

    private Timestamp updatedDate;
}

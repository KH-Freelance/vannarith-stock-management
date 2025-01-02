package com.hfsolution.feature.stockmanagement.dto.purchase;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PurchaseDto {
    
    private Long id;

    private Customer customer;


    private User user; 

    private Long qty;

    private BigDecimal discount;

    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal total = BigDecimal.ZERO;

    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal remainingPayment = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.CASH;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    private String location;


    private String purchaseCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp updatedDate;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Customer{
        long id;
        String customerName;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class User{
        long id;
        String firstname;
        String lastname;
    }
}

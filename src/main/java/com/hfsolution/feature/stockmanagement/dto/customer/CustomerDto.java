package com.hfsolution.feature.stockmanagement.dto.customer;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class CustomerDto {

    private Long id;

    private String customerName;

    private String email;

    private String phone;

    private String address;

    private BigDecimal discount;

    private BigDecimal credit = BigDecimal.ZERO;

    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;


    

}
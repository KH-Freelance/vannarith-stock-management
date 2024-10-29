package com.hfsolution.feature.stockmanagement.dto.request;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomerRequest {

    private String customerName;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime updatedDate;

}

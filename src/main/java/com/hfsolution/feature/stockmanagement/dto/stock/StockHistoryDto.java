package com.hfsolution.feature.stockmanagement.dto.stock;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.feature.stockmanagement.dto.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockHistoryDto {

    private Long id;

    private User user;

    private Long qty;

    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;

   
}

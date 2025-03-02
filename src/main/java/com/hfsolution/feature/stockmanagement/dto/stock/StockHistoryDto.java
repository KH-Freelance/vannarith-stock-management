package com.hfsolution.feature.stockmanagement.dto.stock;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockHistoryDto {

    private Long id;

    private User user;

    private StockDetailDto stock;

    private Long qty;

    private String remark;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class User{
        String firstname;
        String lastname;
    } 
   
}

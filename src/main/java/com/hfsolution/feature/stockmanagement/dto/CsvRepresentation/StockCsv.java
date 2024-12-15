package com.hfsolution.feature.stockmanagement.dto.CsvRepresentation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.feature.stockmanagement.dto.stock.StockHistoryDto;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private Long qty;

    @CsvBindByPosition(position = 2)
    private String firstname;

    @CsvBindByPosition(position = 3)
    private String lastname;

    @CsvBindByPosition(position = 4)
    private Long productId;

    @CsvBindByPosition(position = 5)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @CsvBindByPosition(position = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @CsvBindByPosition(position = 7)
    private Long stockHistoryId;

    @CsvBindByPosition(position = 8)
    private Long stockHistoryQty;

    @CsvBindByPosition(position = 9)
    private String remark;

    @CsvBindByPosition(position = 10)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp stockHistoryCreatedDate;

    


}
package com.hfsolution.feature.stockmanagement.dto.CsvRepresentation;
import java.math.BigDecimal;
import java.sql.Timestamp;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class CustomerCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private String customerName;

    @CsvBindByPosition(position = 2)
    private String email;

    @CsvBindByPosition(position = 3)
    private String phone;

    @CsvBindByPosition(position = 4)
    private String address;

    @CsvBindByPosition(position = 5)
    private BigDecimal discount;

    @CsvBindByPosition(position = 6)
    private BigDecimal credit;

    @CsvBindByPosition(position = 7)
    private Timestamp createdDate;

    @CsvBindByPosition(position = 8)
    private Timestamp updatedDate;
}
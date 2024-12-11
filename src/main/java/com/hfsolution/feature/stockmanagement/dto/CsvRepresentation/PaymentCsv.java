package com.hfsolution.feature.stockmanagement.dto.CsvRepresentation;

import java.sql.Timestamp;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Setter
@Getter
public class PaymentCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private Long purchaseId;

    @CsvBindByPosition(position = 2)
    private BigDecimal amount;

    @CsvBindByPosition(position = 3)
    private Timestamp createdDate;

    @CsvBindByPosition(position = 4)
    private Timestamp updateDate;

}

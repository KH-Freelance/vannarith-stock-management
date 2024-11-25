package com.hfsolution.feature.stockmanagement.dto.csvrepresentation;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class PurchaseCsv {

    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByPosition(position = 1)
    private Long productId;

    @CsvBindByPosition(position = 2)
    private String productName;

    @CsvBindByPosition(position = 3)
    private Long customerId;

    @CsvBindByPosition(position = 4)
    private String customerName;

    @CsvBindByPosition(position = 5)
    private Long employeeId;

    @CsvBindByPosition(position = 6)
    private String employeeName;

    @CsvBindByPosition(position = 7)
    private Long qty;

    @CsvBindByPosition(position = 8)
    private BigDecimal total;

    @CsvBindByPosition(position = 9)
    private PaymentType paymentType;

    @CsvBindByPosition(position = 10)
    private PaymentStatus paymentStatus;

    @CsvBindByPosition(position = 11)
    private String location;

    @CsvBindByPosition(position = 12)
    private BigDecimal discount;

    @CsvBindByPosition(position = 13)
    private Timestamp createdDate;

    @CsvBindByPosition(position = 14)
    private Timestamp updateDate;

}
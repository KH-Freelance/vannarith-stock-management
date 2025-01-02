package com.hfsolution.feature.stockmanagement.dto.purchase;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.dto.customer.CustomerDto;
import com.hfsolution.feature.stockmanagement.dto.user.User;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PurchaseDetailDto {

    private Long id;

    private List<PurchaseItemDto> purchaseItems = new ArrayList<>();

    private CustomerDto customer;

    private User user; 

    private List<PaymentDto> payments = new ArrayList<>();

    private Long qty;

    private BigDecimal discount;

    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal total;

    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal remainingPayment;

    private PaymentType paymentType = PaymentType.CASH;

    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    private String location;

    private String purchaseCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

}

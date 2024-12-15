package com.hfsolution.feature.stockmanagement.dto.CsvRepresentation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import com.opencsv.bean.CsvBindByPosition;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseCsv {

    private Long id;

    // private List<PurchaseItem> purchaseItems = new ArrayList<>();


    // private Customer customer;

    // private User user; 

    private List<Payment> payments = new ArrayList<>();

    private Long qty;

    private BigDecimal total;

    private PaymentType paymentType = PaymentType.CASH;

    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    private String location;

    private String purchaseCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;


}
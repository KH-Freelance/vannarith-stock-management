package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import com.opencsv.bean.CsvBindByPosition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 

    @CsvBindByPosition(position = 1)
    private Long productId;

    @CsvBindByPosition(position = 2)
    private Long customerId;

    @CsvBindByPosition(position = 3)
    private Long userId;

    @Column(name = "qty")
    @CsvBindByPosition(position = 4)
    private Long qty;

    @Column(name = "total")
    @CsvBindByPosition(position = 5)
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal total;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    @CsvBindByPosition(position = 6)
    private PaymentType paymentType = PaymentType.CASH;

    @Column(name = "discount")
    @CsvBindByPosition(position = 7)
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal discount;

    @Column(name = "created_date")
    @CsvBindByPosition(position = 8)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @CsvBindByPosition(position = 9)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp updateDate;


    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if(this.discount==null){
            this.discount = BigDecimal.valueOf(0.00);
        }
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if(this.updateDate==null){
            this.updateDate = new Timestamp(System.currentTimeMillis());
        }
    }



}

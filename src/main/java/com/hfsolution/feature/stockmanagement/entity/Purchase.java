package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.enums.PaymentType;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 

    @Column(name = "qty")
    private Long qty;

    @Column(name = "total")
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal total;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.CASH;

    @Column(name = "discount")
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal discount;

    @Column(name = "created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @Column(name = "updated_date")
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

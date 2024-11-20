package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Purchase")
public class Purchase {

    @Id
    @Column(name = "id")
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

    @OneToMany(mappedBy = "purchase", fetch = FetchType.EAGER)
    private List<Payment> payments;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "total")
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal total;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType = PaymentType.CASH;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    @Column(name = "location")
    private String location;

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
            this.discount = BigDecimal.ZERO;
        }
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if(this.updateDate==null){
            this.updateDate = new Timestamp(System.currentTimeMillis());
        }
    }

}

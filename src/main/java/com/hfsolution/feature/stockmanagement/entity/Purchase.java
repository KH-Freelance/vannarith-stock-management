package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_seq")
    @SequenceGenerator(name = "purchase_seq", sequenceName = "purchase_id_seq", allocationSize = 1)
    @Column(name = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @CsvIgnore
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @CsvIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @CsvIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 

    @Transient // This field will not be persisted in the database
    @JsonIgnore
    @CsvBindByPosition(position = 1)
    private Long prodId;

    @Transient // This field will not be persisted in the database
    @JsonIgnore
    @CsvBindByPosition(position = 2)
    private Long custId;

    @Transient // This field will not be persisted in the database
    @JsonIgnore
    @CsvBindByPosition(position = 3)
    private Long usrId;

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

    @PostLoad
    public void postLoad() {
        // Populate the productId field when the entity is loaded from the database
        if (this.product != null) {
            this.prodId = this.product.getId(); // Assuming Product has a getId() method
        }
        if (this.user != null) {
            this.usrId = this.user.getId(); // Assuming Product has a getId() method
        }
        if (this.customer != null) {
            this.custId = this.customer.getId(); // Assuming Product has a getId() method
        }
    }

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

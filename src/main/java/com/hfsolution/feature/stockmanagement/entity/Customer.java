package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.opencsv.bean.CsvBindByPosition;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @Column(name = "customer_name")
    @CsvBindByPosition(position = 1)
    private String customerName;

    @Column(name = "email")
    @CsvBindByPosition(position = 2)
    private String email;

    @Column(name = "phone")
    @CsvBindByPosition(position = 3)
    private String phone;

    @Column(name = "address")
    @CsvBindByPosition(position = 4)
    private String address;

    @Column(name = "discount")
    @CsvBindByPosition(position = 5)
    private BigDecimal discount;

    @Column(name = "credit")
    @CsvBindByPosition(position = 6)
    private BigDecimal credit;

    @Column(name = "created_date")
    @CsvBindByPosition(position = 7)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @CsvBindByPosition(position = 8)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp updatedDate;

    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if(this.discount==null){
            this.discount = BigDecimal.valueOf(0.00);
        }
        if(this.credit==null){
            this.credit = BigDecimal.valueOf(0.00);
        }
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if(this.updatedDate==null){
            this.updatedDate = new Timestamp(System.currentTimeMillis());
        }
    }

}
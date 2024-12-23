package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.app.util.TimestampConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "Customer")
public class Customer {

    @Id
    @Column(name = "id")
    @ExcelProperty("id")
    private Long id;

    @Column(name = "customer_name",unique = true)
    @ExcelProperty("customer_name")
    private String customerName;

    @Column(name = "email")
    @ExcelProperty("email")
    private String email;

    @Column(name = "phone")
    @ExcelProperty("phone")
    private String phone;

    @Column(name = "address")
    @ExcelProperty("address")
    private String address;

    @Column(name = "discount")
    @ExcelProperty("discount")
    private BigDecimal discount;

    @Transient
    private BigDecimal credit = BigDecimal.ZERO;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(value = "created_date",converter = TimestampConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(value = "updated_date",converter = TimestampConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @ExcelIgnore
    private List<Purchase> purchases = new ArrayList<>();

    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if(this.discount==null){
            this.discount = BigDecimal.ZERO;
        }
        // if(this.credit==null){
        //     this.credit = BigDecimal.ZERO;
        // }
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if(this.updatedDate==null){
            this.updatedDate = new Timestamp(System.currentTimeMillis());
        }
    }

}
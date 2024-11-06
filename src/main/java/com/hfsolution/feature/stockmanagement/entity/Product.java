package com.hfsolution.feature.stockmanagement.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hfsolution.feature.token.entity.Token;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import com.opencsv.bean.CsvDate;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "Product")
public class Product {

    @Id
    @Column(name = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @Column(name = "product_name")
    @CsvBindByPosition(position = 1)
    private String productName;

    @Column(name = "product_desc")
    @CsvBindByPosition(position = 2)
    private String productDesc;

    @Column(name = "price")
    @CsvBindByPosition(position = 3)
    private BigDecimal price;

    @Column(name = "discount")
    @CsvBindByPosition(position = 4)
    private BigDecimal discount;
    
    @Column(name = "created_date")
    @CsvDate(value = "yyyy-MM-dd hh:mm:ss")
    @CsvBindByPosition(position = 5)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @CsvDate(value = "yyyy-MM-dd hh:mm:ss")
    @CsvBindByPosition(position = 6)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp updatedDate;

    @Column(name = "expiry_date")
    @CsvBindByPosition(position = 7)
    @CsvDate(value = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Timestamp expiryDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Stock stock;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Purchase purchase;


    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if(this.discount==null){
            this.discount = BigDecimal.ZERO;
        }
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if(this.updatedDate==null){
            this.updatedDate = new Timestamp(System.currentTimeMillis());
        }
    }
  
}

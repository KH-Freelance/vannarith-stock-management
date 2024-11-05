package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq")
    @SequenceGenerator(name = "stock_seq", sequenceName = "stock_id_seq", allocationSize = 1)
    @Column(name = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @CsvIgnore
    private Product product;

    @Transient // This field will not be persisted in the database
    @JsonIgnore
    @CsvBindByPosition(position = 1)
    private Long prodId;

    @CsvBindByPosition(position = 2)
    @Column(name = "qty")
    private Long qty;

    @Column(name = "created_date")
    @CsvBindByPosition(position = 3)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @CsvBindByPosition(position = 4)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Timestamp updatedDate;
    
    @PostLoad
    public void postLoad() {
        // Populate the productId field when the entity is loaded from the database
        if (this.product != null) {
            this.prodId = this.product.getId(); // Assuming Product has a getId() method
        }
    }

    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if(this.updatedDate==null){
            this.updatedDate = new Timestamp(System.currentTimeMillis());
        }
    }
    

}

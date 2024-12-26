package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hfsolution.app.util.TimestampConverter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
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
    @Column(name = "id")
    private Long id;


    @Column(name = "product_id")
    private Long productId;

    @Column(name = "batch_id")
    private String batchId;

    @ManyToOne(fetch = FetchType.EAGER,optional  =true)
    @JoinColumn(name = "product_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "factory") 
    private String factory;

    @Column(name = "factory_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(converter = TimestampConverter.class)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp factoryDate;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @ExcelIgnore
    private List<StockHistory> stockHistories = new ArrayList<>();

    @Transient // This field will not be persisted in the database
    @JsonIgnore
    private Double percentage;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(converter = TimestampConverter.class)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(converter = TimestampConverter.class)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @Column(name = "expiry_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(converter = TimestampConverter.class)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Phnom_Penh")
    private Timestamp expiryDate;

    public void removeStockHistory(StockHistory stockHistory) {
        this.stockHistories.remove(stockHistory);
    }

    public void addStockHistory(StockHistory stockHistory) {
        if (this.stockHistories == null) {
            this.stockHistories = new ArrayList<>();
        }
        this.stockHistories.add(stockHistory);
        stockHistory.setStock(this);
    }

    public boolean hasProduct() {
        return this.product != null;
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

package com.hfsolution.feature.stockmanagement.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "product_history")
public class ProductHistory {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_desc")
    private String productDesc;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "factory")
    private String factory;

    @Column(name = "import_price")
    private BigDecimal importPrice;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    // @Column(name = "expiry_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Phnom_Penh")
    // private Timestamp expiryDate;

    @Column(name = "deleted_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp deletedDate;

    @Column(name = "image_url")
    private String imageUrl;

    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if (this.discount == null) {
            this.discount = BigDecimal.ZERO;
        }
        if (this.createdDate == null) {
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        if (this.updatedDate == null) {
            this.updatedDate = new Timestamp(System.currentTimeMillis());
        }
    }

   
}

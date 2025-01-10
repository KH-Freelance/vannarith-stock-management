package com.hfsolution.feature.stockmanagement.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.app.util.TimestampConverter;

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
@Table(name = "Product")
@SQLDelete(sql = "UPDATE Product SET deleted = true WHERE id = ?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "deleted", type = Boolean.class))
@Filter(name = "deletedProductFilter", condition = "deleted = :deleted")
public class Product {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_desc")
    private String productDesc;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(converter = TimestampConverter.class)
    

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @ExcelProperty(converter = TimestampConverter.class)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @Column(name = "image_url")
    private String imageUrl;

    private boolean deleted;

    @PrePersist
    public void preInsert() {
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

package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Return")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ReturnItem> returnItems = new ArrayList<>();

    @Column(name = "total_qty")
    private Long totalQty;

    @Column(name = "total_amount", nullable = false) 
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal totalAmount;

    @Column(name = "refund_amount") 
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal refundAmount;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    public void addReturnItem(ReturnItem returnItem) {
        returnItems.add(returnItem);
        returnItem.setReturnEntity(this);
    }

    @PrePersist
    public void preInsert() {
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
    }

}
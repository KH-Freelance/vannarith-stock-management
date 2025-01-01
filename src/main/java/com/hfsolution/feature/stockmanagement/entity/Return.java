package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.user.entity.User;

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
@Table(name = "return")
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "source_purchase_code", nullable = false)
    private String sourcePurchaseCode;

    @Column(name = "target_purchase_code", nullable = false)
    private String targetPurchaseCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 

    @OneToMany(mappedBy = "returnEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ReturnItem> returnItems = new ArrayList<>();

    @Column(name = "return_type", nullable = false)
    private String returnType;

    @Column(name = "refund_amount") 
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal refundAmount;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    // @Column(name = "total_qty")
    // private Long totalQty;

    // @Column(name = "total_amount", nullable = false) 
    // @JsonSerialize(using = BigDecimalSerializer.class) 
    // private BigDecimal totalAmount;

    public void addReturnItem(ReturnItem returnItem) {
        returnItems.add(returnItem);
        returnItem.setReturnEntity(this);
    }

    public Long calculateTotalQty() {
        return returnItems.stream().mapToLong(ReturnItem::getQty).sum();
    }

    public BigDecimal calculateTotalPrice() {
        return returnItems.stream()
                .map(ReturnItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    @PrePersist
    public void preInsert() {
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
    }

}
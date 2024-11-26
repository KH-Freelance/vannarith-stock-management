package com.hfsolution.feature.stockmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;

@Data
@Entity
@Table(name = "purchase_item")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "purchase_id", nullable = false)
    @JsonIgnore
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    // @JsonIgnore
    private Product product;

    @Column(name = "qty", nullable = false)
    private Long qty;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "discount")
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal discount;

}

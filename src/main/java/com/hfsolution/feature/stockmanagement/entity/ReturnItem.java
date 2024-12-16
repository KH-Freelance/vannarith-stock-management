package com.hfsolution.feature.stockmanagement.entity;


import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "return_item")
public class ReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "return_id", nullable = false)
    private Return returnEntity; 

    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "product_id", nullable = false)
    // private Product product; 

    // @OneToOne(fetch = FetchType.LAZY, optional = true)
    // @JoinColumn(name = "product_id", nullable = false, updatable = false, insertable = false)
    // private Product product;

    @OneToOne(fetch = FetchType.EAGER,optional  =true)
    @JoinColumn(name = "product_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Product product;


    @Column(name = "product_id")
    private Long productId;

    @Column(name = "qty", nullable = false)
    private Long qty; 

    @Column(name = "amount", nullable = false) 
    @JsonSerialize(using = BigDecimalSerializer.class) 
    private BigDecimal amount;

}

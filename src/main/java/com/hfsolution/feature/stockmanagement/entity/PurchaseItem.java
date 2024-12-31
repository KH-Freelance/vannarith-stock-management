package com.hfsolution.feature.stockmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@Entity
@Table(name = "purchase_item")
public class PurchaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    @JsonIgnore
    private Purchase purchase;

    // @ManyToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "product_id", nullable = false)
    // // @JsonIgnore
    // private Product product;

    // @OneToOne(fetch = FetchType.LAZY, optional = true)
    // @JoinColumn(name = "product_id", nullable = false, updatable = false, insertable = false)
    // private Product product;

    @OneToOne(fetch = FetchType.EAGER,optional  = true,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "batch_id")
    private String batchId;

    @Column(name = "status")
    private String status;

    @Column(name = "qty", nullable = false)
    private Long qty;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "discount")
    private BigDecimal discount;

    @PrePersist
    public void preInsert() {
        if(this.status==null || this.status.isBlank() || this.status.isEmpty()){
            this.status =  "ACTIVE";
        }
    }


}

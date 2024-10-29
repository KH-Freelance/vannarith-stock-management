package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "user_id")
    private Long userId; 

    @Column(name = "qty")
    private Long qty;

    @Column(name = "created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp updateDate;




}

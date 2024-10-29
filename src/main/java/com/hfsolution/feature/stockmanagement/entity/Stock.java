package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hfsolution.app.util.AppTools;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id", nullable = false, insertable = false, updatable = false)
    private Product product;

    @JsonIgnore
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp updatedDate;

    



}

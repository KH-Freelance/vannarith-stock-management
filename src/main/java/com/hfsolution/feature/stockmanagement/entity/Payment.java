package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hfsolution.app.util.BigDecimalSerializer;
import com.hfsolution.feature.user.entity.User;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "Payment")
public class Payment {

    @Id
    @Column(name = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @CsvIgnore
    @JsonIgnore
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Transient // This field will not be persisted in the database
    @JsonIgnore
    @CsvBindByPosition(position = 1)
    private Long purchId;

    @Column(name = "amount")
    @CsvBindByPosition(position = 2)
    private BigDecimal amount;

    @Column(name = "created_date")
    @CsvBindByPosition(position = 3)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @CsvBindByPosition(position = 4)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMMM dd, yyyy h:mm a")
    private Timestamp updatedDate;

    @PrePersist
    public void preInsert() {
        
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
        // Always update datetime if insert or update
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }

}

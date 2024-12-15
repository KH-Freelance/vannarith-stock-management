package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.feature.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_history")
public class StockHistory {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stock stock;

    private String firstname;

    private String lastname;

    @Column(name = "qty")
    private Long qty;

    @Column(name = "remark")
    private String remark;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @PrePersist
    public void preInsert() {
        // Set default values or modify fields before inserting
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
    }
}

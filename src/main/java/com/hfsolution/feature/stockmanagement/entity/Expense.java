package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.ManyToAny;

import java.math.BigDecimal;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.hfsolution.app.util.TimestampConverter;
import com.hfsolution.feature.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "remark")
    private String remark;

    @ManyToOne(fetch = FetchType.EAGER,optional  = true,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false, insertable = false)
    private User user;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp createdDate;

    @Column(name = "updated_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp updatedDate;

    @PrePersist
    public void preInsert() {
        if(this.createdDate==null){
            this.createdDate = new Timestamp(System.currentTimeMillis());
        }
    }

}
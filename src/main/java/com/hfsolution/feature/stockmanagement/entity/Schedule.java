package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "function_type", nullable = false, length = 10)
    private String functionType; 

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    @Column(name = "schedule_date", nullable = false)
    private Timestamp scheduleDate;

    @Column(name = "schedule_type", nullable = false, length = 10)
    private String scheduleType; 

    @Column(name = "active", nullable = false)
    private Boolean active ; 

    @Column(name = "execute", nullable = false)
    private Boolean execute ; 

    @Column(name = "chat_id", length = 100)
    private String chatId; 

    @Column(name = "telegram_token", length = 500)
    private String telegramToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    @Column(name = "last_executed_date", nullable = false)
    private Timestamp lastExecuteDate;

}

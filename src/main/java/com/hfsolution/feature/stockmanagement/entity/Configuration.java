package com.hfsolution.feature.stockmanagement.entity;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.hfsolution.app.util.AppTools;
import jakarta.persistence.Column;
import jakarta.persistence.Entity; 
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "configuration")
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "function_type")
    private String functionType; 

    @Column(name = "time")
    private String time; 

    @Column(name = "day_of_week")
    private String dayOfWeek; 

    @Column(name = "day_of_month")
    private Long dayOfMonth; 
   
    @Column(name = "schedule_type")
    private String scheduleType; 

    @Column(name = "active")
    private Boolean active ; 

    // @Column(name = "execute", nullable = false)
    // private Boolean execute ; 

    @Column(name = "chat_id")
    private String chatId; 

    @Column(name = "telegram_token")
    private String telegramToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    @Column(name = "last_executed_date", nullable = false)
    private Timestamp lastExecutedDate;

  
    

    
    public String toJson() {
        try {
            return new Gson().toJson(this);
        } catch (Exception e) {
            
        }
        return "";
        
    }

    // @PrePersist
    // public void preInsert() {
    //     if(this.dayOfWeek==null){
    //         this.dayOfWeek = "N/A";
    //     }
    //     if(this.scheduleType==null){
    //         this.scheduleType = "N/A";
    //     }
    //     if(this.dayOfMonth==null){
    //         this.dayOfMonth = 0l;
    //     }
    // }




}
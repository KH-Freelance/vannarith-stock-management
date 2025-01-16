package com.hfsolution.feature.stockmanagement.dto.configuration;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ConfigurationDto {
    private Long id;
    private String functionType; 
    private String time; 
    private String dayOfWeek; 
    private Long dayOfMonth; 
    private String scheduleType; 
    private Boolean active; 
    private String chatId; 
    private String telegramToken;
    private String channel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Phnom_Penh")
    private Timestamp lastExecutedDate;
}

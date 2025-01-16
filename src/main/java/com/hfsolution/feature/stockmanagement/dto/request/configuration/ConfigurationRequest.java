package com.hfsolution.feature.stockmanagement.dto.request.configuration;


import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ConfigurationRequest {

    @Pattern(
        regexp = "^(WEEKLY|DAILY|MONTHLY)$",
        message = "Schedule Type must be one of the following: WEEKLY, DAILY, MONTHLY."
    )
    //@NotBlank(message = "Schedule Type is required.")
    private String scheduleType;
    //@NotBlank(message = "Telegram Tokenis required.")
    private String telegramToken;
    //@NotBlank(message = "Chat ID is required.")
    private String chatId;
    private boolean active;
    @Pattern(
    regexp = "^\\d{2}:\\d{2}$",
    message = "Time must be in the format HH:mm."
    )
    //@NotBlank(message = "Time is required.")
    private String time;
    private Long dayOfMonth;
    private String dayOfWeek;
    
}

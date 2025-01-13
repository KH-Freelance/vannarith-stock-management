package com.hfsolution.feature.stockmanagement.dto.request.schedule;


import java.math.BigDecimal;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ScheduleRequest {

    @Pattern(
        regexp = "^(WEEKLY|DAILY|MONTHLY)$",
        message = "Schedule Type must be one of the following: WEEKLY, DAILY, MONTHLY."
    )
    private String scheduleType;
    private String telegramToken;
    private String chatId;
    private boolean active;
    @Pattern(
    regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$",
    message = "Schedule datetime must be in the format yyyy-MM-dd HH:mm."
    )
    private String scheduleDate;
    
}

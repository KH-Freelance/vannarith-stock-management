package com.hfsolution.feature.stockmanagement.dto.report;

import java.sql.Timestamp;

import org.apache.poi.ss.formula.functions.T;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hfsolution.app.util.TimestampConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnDto {
    @JsonProperty("Sales")
    @ExcelProperty("Sales")
    private String sales;
    @JsonProperty("Customer")
    @ExcelProperty("Customer")
    private String customer;
    @JsonProperty("Type")
    @ExcelProperty("Type")
    private String type;
    @JsonProperty("Returned To Sales")
    @ExcelProperty("Returned To Sales")
    private String returnedToSales;
    @JsonProperty("Returned By")
    @ExcelProperty("Returned By")
    private String returnedBy;
    @JsonProperty("Returned At")
    @ExcelProperty(value = "Returned At",converter = TimestampConverter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Phnom_Penh")
    private Timestamp returnedAt;
}

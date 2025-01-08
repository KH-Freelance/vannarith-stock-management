package com.hfsolution.feature.stockmanagement.controller;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hfsolution.feature.stockmanagement.enums.ReportSaleType;
import com.hfsolution.feature.stockmanagement.service.report.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    final ReportService reportService;
    @GetMapping("/stock")
    public Object reportStock( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate
        ) {
        return reportService.reportStock(startDate, endDate);
    }

    @GetMapping("/excel-stock")
    public Object excelReportStock( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate
        ) {
        return reportService.excelReportStock(startDate, endDate);
    }

    @GetMapping("/customer")
    public Object reportCustomer( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate
        ) {
        return reportService.reportCustomer(startDate, endDate);
    }

    @GetMapping("/excel-customer")
    public Object excelReportCustomer( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate
        ) {
        return reportService.excelReportCustomer(startDate, endDate);
    }

    @GetMapping("/excel-all")
    public Object excelReportCombined( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false)  String productName
        ) {
        return reportService.excelReportCombined(startDate, endDate,productName,customerName);
    }

    @GetMapping("/purchase")
    public Object reportPurchase( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate
        ) {
        return reportService.reportPurchase(startDate, endDate);
    }
    
    @GetMapping("/sales")
    public Object reportSale( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false)  String productName
        ) {
        return reportService.reportSale(startDate, endDate,productName,customerName);
    }

    @GetMapping("/return")
    public Object reportSale( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate,
        @RequestParam(required = false)  String customerName
        ) {
        return reportService.reportReturn(startDate, endDate,customerName);
    }

    @GetMapping("/excel-return")
    public Object excelReportReturn( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate,
        @RequestParam(required = false)  String customerName
        ) {
        return reportService.excelReportReturn(startDate, endDate,customerName);
    }

    @GetMapping("/excel-sales")
    public Object excelReportSale( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate,
        @RequestParam(required = false)  String customerName,
        @RequestParam(required = false) String productName
        ) {
        return reportService.excelReportSale(startDate, endDate,productName,customerName);
    }
}

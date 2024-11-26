package com.hfsolution.feature.stockmanagement.controller;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public Object searchStockHistory( 
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "Start Date must be in the format yyyy-MM-dd HH:mm:ss")
        String startDate,
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", message = "End Date must be in the format yyyy-MM-dd HH:mm:ss")
        String endDate
        ) {
        // return stockService.searchHistory(id,pageNo,pageSize,sort,sortByColum);
        return reportService.reportStock(startDate, endDate);
    }
}

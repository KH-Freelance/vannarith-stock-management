package com.hfsolution.feature.stockmanagement.service.report;

import java.io.File;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReportService {
    public Object reportStock(String startDate, String endDate);
    public ResponseEntity<Void> excelReportStock(String startDate, String endDate);
    public ResponseEntity<Void> excelReportCustomer(String startDate, String endDate);
    public ResponseEntity<Void> excelReportReturn(String startDate, String endDate, String customerName);
    public ResponseEntity<Void> excelReportCombined(String startDate, String endDate, String customerName, String productName);
    public ResponseEntity<Void> excelReportSale(String startDate, String endDate,String productName, String customerName);
    public Object reportCustomer(String startDate, String endDate);
    public Object reportPurchase(String startDate, String endDate);
    public Object reportSale(String startDate, String endDate,String productName, String customerName);
    public Object reportReturn(String startDate, String endDate, String customerName);
    //public File excelReportSaleTest(String startDate, String endDate, String productName, String customerName) ;
}

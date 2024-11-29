package com.hfsolution.feature.stockmanagement.service.report;

import org.springframework.stereotype.Service;

@Service
public interface ReportService {
    public Object reportStock(String startDate, String endDate);
    public Object reportCustomer(String startDate, String endDate);
    public Object reportPurchase(String startDate, String endDate);
}

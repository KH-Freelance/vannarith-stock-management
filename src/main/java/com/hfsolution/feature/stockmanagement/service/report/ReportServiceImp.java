package com.hfsolution.feature.stockmanagement.service.report;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.entity.Stock;

import static com.hfsolution.app.constant.AppConstant.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImp  implements ReportService{
    
    private final StockDao stockDao;
    private final ProductDao productDao;
    private final HttpServletRequest httpServletRequest;

    @Override
    public Object reportStock(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT STOCK");
        try {
            Specification<Stock> stocks = new CustomSpecification<>(buildQuerySQL(startDate,endDate));
            BaseEntityResponseDto<Stock> stockResult = stockDao.searchStock(stocks);
            return stockResult;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
       
    }

    private String buildQuerySQL(String start, String end){
        // Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(start)));
        // Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(end), LocalTime.parse(end, null)));
        return String.format("createdDate=[%s~%s]", start,end);
    }
    
}

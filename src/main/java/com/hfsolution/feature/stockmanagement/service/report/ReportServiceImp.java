package com.hfsolution.feature.stockmanagement.service.report;

import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.dto.response.ReportCustomerDto;
import com.hfsolution.feature.stockmanagement.dto.response.ReportStockDto;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;

import static com.hfsolution.app.constant.AppConstant.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImp  implements ReportService{
    
    private final StockDao stockDao;
    private final StockHistoryDao stockHistoryDao;
    private final PurchaseDao purchaseDao;
    private final CustomerDao customerDao;
    private final ProductDao productDao;
    private final HttpServletRequest httpServletRequest;

    @Override
    public Object reportStock(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT STOCK");
        SuccessResponse<Object> response = new SuccessResponse<>();
        try {

            BaseEntityResponseDto<Stock> stockResult = stockDao.findAll();
            if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntityList()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }
            List<ReportStockDto> reportStockDtos = new ArrayList<>();
            Long totalQty = stockDao.getTotal();
            for (Stock stock : stockResult.getEntityList()) {
                long stockSaled = 0;
                Timestamp start = Timestamp.valueOf(startDate);
                Timestamp end = Timestamp.valueOf(endDate);

                
                for (StockHistory stockHistory : stockHistoryDao.findAllByStockId(stockSaled).getEntityList()) {
                    if(stockHistory.getCreatedDate().after(start) && stockHistory.getCreatedDate().before(end)){
                        if(stockHistory.getQty() < 0){
                            stockSaled +=(stockHistory.getQty()*-1);
                        }
                    }
                }
                
                ReportStockDto product = new ReportStockDto();   
                double percentage = ((double) stock.getQty() / totalQty) * 100;
                BigDecimal bd = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
                
                product.setProductId(stock.getProduct().getId());
                product.setStockId(stock.getId());
                product.setTotalAsset(bd.doubleValue());
                product.setProductName(stock.getProduct().getProductName());
                product.setSalePrice(stock.getProduct().getPrice());
                product.setImportPrice(stock.getProduct().getImportPrice());
                product.setFactory(stock.getProduct().getFactory());
                product.setStockOnHand(stock.getQty());
                product.setStockSold(stockSaled);
                product.setDiscount(stock.getProduct().getDiscount());
                product.setCreatedDate(stock.getProduct().getCreatedDate());
                product.setExpiryDate(stock.getProduct().getExpiryDate());
                reportStockDtos.add(product);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(reportStockDtos);
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
       
    }

    @Override
    public Object reportCustomer(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT CUSTOMER");
        SuccessResponse<Object> response = new SuccessResponse<>();
        try {
            BaseEntityResponseDto<Customer> customerResult = customerDao.findAll();
            if(!customerResult.getStatus().equals(SUCCESS) || customerResult.getEntityList()==null){
                String msg = AppTools.appGetMessage("015");
                throw new AppException("015",msg);
            }
            List<ReportCustomerDto> reportCustomerDtos =  new ArrayList<>();
            for (Customer customer : customerResult.getEntityList()) {
                ReportCustomerDto cusomerReport = new ReportCustomerDto();
                cusomerReport.setCustomerId(customer.getId());
                cusomerReport.setCustomerName(customer.getCustomerName());
                cusomerReport.setCurrentCredit(customer.getCredit());

                BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.findPurchaseByCustomerIdAndCreatedDateBetween(customer.getId(),startDate,endDate);
                ReportCustomerDto.CreditCategoies creditCategoies = new ReportCustomerDto.CreditCategoies();

                
                for (Purchase purchase : purchaseResult.getEntityList()) {
                    if(purchase.getPaymentType().compareTo(PaymentType.CASH) == 0 || purchase.getPaymentStatus().compareTo(PaymentStatus.PAID) == 0) continue;
                        
                    BigDecimal totalAoumtPaid = BigDecimal.ZERO;
                    for (Payment payment : purchase.getPayments()) {
                        totalAoumtPaid = totalAoumtPaid.add(payment.getAmount());
                    }
                    BigDecimal totalCredit = purchase.getTotal().subtract(totalAoumtPaid);
                    if(purchase.getPaymentType().compareTo(PaymentType.ONE_WEEK) == 0 ||
                        purchase.getPaymentType().compareTo(PaymentType.TWO_WEEK) == 0 ||
                        purchase.getPaymentType().compareTo(PaymentType.THREE_WEEK) == 0 ||
                        purchase.getPaymentType().compareTo(PaymentType.ONE_MONTH) == 0
                    ){
                        creditCategoies.setCreditDay1To30(creditCategoies.getCreditDay1To30().add(totalCredit));
                    }else if(purchase.getPaymentType().compareTo(PaymentType.TWO_MONTH) == 0){
                        creditCategoies.setCreditDay31To60(creditCategoies.getCreditDay31To60().add(totalCredit));
                    }else if(purchase.getPaymentType().compareTo(PaymentType.THREE_MONTH) == 0){
                        creditCategoies.setCreditDay61To90(creditCategoies.getCreditDay61To90().add(totalCredit));
                    }else {
                        creditCategoies.setCreditMoreThan90(creditCategoies.getCreditMoreThan90().add(totalCredit));
                    }
                }
                BigDecimal totalCredit = creditCategoies.getCreditDay1To30().add(creditCategoies.getCreditDay31To60().add(creditCategoies.getCreditDay61To90().add(creditCategoies.getCreditMoreThan90())));
                cusomerReport.setCreditCategoies(creditCategoies);
                cusomerReport.setCurrentCredit(customer.getCredit());
                cusomerReport.setTotalCredit(totalCredit);
                reportCustomerDtos.add(cusomerReport);

            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(reportCustomerDtos);
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }
}

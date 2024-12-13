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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
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
import com.hfsolution.feature.stockmanagement.dto.report.CustomerDto;
import com.hfsolution.feature.stockmanagement.dto.report.PaymentSummary;
import com.hfsolution.feature.stockmanagement.dto.report.PurchaseDto;
import com.hfsolution.feature.stockmanagement.dto.report.PurchaseItem;
import com.hfsolution.feature.stockmanagement.dto.report.ReportCustomerDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReportPurchase;
import com.hfsolution.feature.stockmanagement.dto.report.ReportPurchaseDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReportStockDto;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseSummaryDTO;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;

import static com.hfsolution.app.constant.AppConstant.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImp  implements ReportService{
    
    private final StockDao stockDao;
    private final ProductDao productDao;
    private final StockHistoryDao stockHistoryDao;
    private final PurchaseDao purchaseDao;
    private final CustomerDao customerDao;
    private final HttpServletRequest httpServletRequest;

    @Override
    @Transactional
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

                
                for (StockHistory stockHistory : stockHistoryDao.findAllByStockId(stock.getId()).getEntityList()) {
                    if(stockHistory.getCreatedDate().after(start) && stockHistory.getCreatedDate().before(end)){
                        if(stockHistory.getQty() < 0){
                            stockSaled +=(stockHistory.getQty()*-1);
                        }
                    }
                }
                
                ReportStockDto product = new ReportStockDto();   
                double percentage = ((double) stock.getQty() / totalQty) * 100;
                BigDecimal bd = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
                
                BaseEntityResponseDto<Product> productResult = productDao.findById(stock.getProductId());
                
                if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntityList()==null){
                    String msg = AppTools.appGetMessage("024");
                    throw new AppException("024",msg);
                }



                product.setProductId(productResult.getEntity().getId());
                product.setStockId(stock.getId());
                product.setTotalAsset(bd.doubleValue());
                product.setProductName(productResult.getEntity().getProductName());
                product.setSalePrice(productResult.getEntity().getPrice());
                product.setImportPrice(productResult.getEntity().getImportPrice());
                product.setFactory(productResult.getEntity().getFactory());
                product.setStockOnHand(stock.getQty());
                product.setStockSold(stockSaled);
                product.setDiscount(productResult.getEntity().getDiscount());
                product.setCreatedDate(productResult.getEntity().getCreatedDate());
                product.setExpiryDate(productResult.getEntity().getExpiryDate());
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
    @Transactional
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
                ReportCustomerDto.CreditCategories creditCategoies = new ReportCustomerDto.CreditCategories();

                
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
                cusomerReport.setCreditCategories(creditCategoies);
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

    @Override
    @Transactional
    public Object reportPurchase(String startDate, String endDate) {

        httpServletRequest.setAttribute(ACTION,"REPORT CUSTOMER");
        SuccessResponse<Object> response = new SuccessResponse<>();
        try {
            ReportPurchase reportPurchase = new ReportPurchase();
            List<String> columes = new ArrayList<>();
            columes.add("CustomerName");
            columes.addAll(AppTools.generateMonthAndYear(startDate, endDate));

            List<PurchaseSummaryDTO> purchaseResult = purchaseDao.findByPurchaseInDateRange(startDate,endDate).getEntityList();
           
            Map<String, Map<String, BigDecimal>> groupedData = purchaseResult.stream()
            .collect(Collectors.groupingBy(
                PurchaseSummaryDTO::getCustomerName, // Group by vendor (customerName)
                LinkedHashMap::new, // Preserve insertion order
                Collectors.toMap(
                    PurchaseSummaryDTO::getCreatedDate, // Map key: createdDate
                    PurchaseSummaryDTO::getTotalAmountSum  // Map value: totalAmountSum
                )
            ));

            List<HashMap<String, Object>> finalResult = new ArrayList<>();
            groupedData.forEach((vendor, dateMap) -> {
                HashMap<String, Object> data = new HashMap<>();
                
                for (String column : columes) {
                    if(!dateMap.containsKey(column)){
                        data.put(column, BigDecimal.ZERO);
                    }
                }
                
                data.putAll(dateMap);
                
                data.put("CustomerName", vendor);
                finalResult.add(data);
            });

            Map<String, BigDecimal> total = purchaseResult.stream()
            
            .collect(Collectors.groupingBy(
                PurchaseSummaryDTO::getCreatedDate, // Group by vendor (customerName)
                Collectors.mapping(
                    PurchaseSummaryDTO::getTotalAmountSum, // Get totalAmountSum
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add) // Sum the values
                )
            ));
            // Sort
            total = total.entrySet().stream()
            .sorted(Map.Entry.comparingByKey((date1, date2) -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yy");
                LocalDate date1Parsed = LocalDate.parse(date1, formatter);
                LocalDate date2Parsed = LocalDate.parse(date2, formatter);
                return date1Parsed.compareTo(date2Parsed);
            }))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
            
            reportPurchase.setContent(finalResult);
            reportPurchase.setTotals(total);
            reportPurchase.setColumns(columes);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(reportPurchase);
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    // @Override
    // public Object reportPurchase(String startDate, String endDate) {
    //     httpServletRequest.setAttribute(ACTION,"REPORT PURCHASE");
    //     SuccessResponse<Object> response = new SuccessResponse<>();
    //     try {

    //         ReportPurchaseDto reportPurchaseDto = new ReportPurchaseDto();
    //         List<PurchaseDto> purchaseDtos = new ArrayList<>();
    //         long totalProductSoldCount = 0;
    //         BigDecimal totalCostSold = BigDecimal.ZERO;
            
            
    //         BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.findPurchaseByCreatedDateBetween(startDate,endDate);
    //         for (Purchase purchase : purchaseResult.getEntityList()) {
    //             PurchaseDto purchaseDto = new PurchaseDto();
    //             purchaseDto.setId(purchase.getId());
    //             purchaseDto.setQty(purchase.getQty());
    //             purchaseDto.setTotal(purchase.getTotal());
    //             purchaseDto.setPaymentStatus(purchase.getPaymentStatus());
    //             purchaseDto.setPaymentType(purchase.getPaymentType());
    //             purchaseDto.setCreatedDate(purchase.getCreatedDate());
    //             purchaseDto.setLocation(purchase.getLocation());
    //             purchaseDto.setPurchaseCode(purchase.getPurchaseCode());
    //             purchaseDto.setUpdatedDate(purchase.getUpdatedDate());


    //             // Set Customer
    //             CustomerDto customerDto = new CustomerDto();
    //             BeanUtils.copyProperties(purchase.getCustomer(), customerDto);

    //             // Set PaymentSummary
    //             List<PaymentSummary> transactionSummaries = new ArrayList<>();
    //             for (Payment payment : purchase.getPayments()) {
    //                 PaymentSummary paymentSummary = new PaymentSummary();
    //                 paymentSummary.setId(payment.getId());
    //                 paymentSummary.setAmount(payment.getAmount());
    //                 paymentSummary.setCreatedDate(payment.getCreatedDate());
    //                 paymentSummary.setUpdateDate(payment.getUpdateDate());

    //                 // Set TotalCostSold
    //                 totalCostSold = totalCostSold.add(payment.getAmount());

    //                 transactionSummaries.add(paymentSummary);
    //             }

    //             // Set PurchaseItem
    //             List<PurchaseItem> purchaseItems = new ArrayList<>();
    //             for (com.hfsolution.feature.stockmanagement.entity.PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
    //                 PurchaseItem purchaseItemDto = new PurchaseItem();
    //                 purchaseItemDto.setId(purchaseItem.getId());
    //                 purchaseItemDto.setProductDesc(purchaseItem.getProduct().getProductDesc());
    //                 purchaseItemDto.setProductName(purchaseItem.getProduct().getProductName());
    //                 purchaseItemDto.setFactory(purchaseItem.getProduct().getFactory());
    //                 purchaseItemDto.setQty(purchaseItem.getQty());
    //                 purchaseItemDto.setPrice(purchaseItem.getPrice());
    //                 purchaseItemDto.setImportPrice(purchaseItem.getProduct().getImportPrice());
    //                 purchaseItemDto.setDiscount(purchaseItem.getProduct().getDiscount());
    //                 purchaseItemDto.setCreatedDate(purchaseItem.getProduct().getCreatedDate());
    //                 purchaseItemDto.setExpiryDate(purchaseItem.getProduct().getExpiryDate());

    //                 // Set TotalProductSoldCount
    //                 totalProductSoldCount+=purchaseItem.getQty();

    //                 purchaseItems.add(purchaseItemDto);
    //             }
    //             purchaseDto.setTransactionSummaries(transactionSummaries);
    //             purchaseDto.setPurchaseItems(purchaseItems);
    //             purchaseDto.setCustomer(customerDto);
    //             purchaseDtos.add(purchaseDto);
    //         }
    //         reportPurchaseDto.setPurchaseOrders(purchaseDtos);
    //         reportPurchaseDto.setTotalCostSold(totalCostSold);
    //         reportPurchaseDto.setTotalProductSoldCount(totalProductSoldCount);

    //         response.setStatus(SUCCESS);
    //         response.setCode(SUCCESS_CODE);
    //         response.setData(reportPurchaseDto);
    //         return response;
    //     }catch (DatabaseException e) {
    //         throw e;   
    //     }catch (AppException e) {
    //         throw e;   
    //     }catch(Exception e){
    //         throw new AppException(FAIL_CODE,e.getMessage(),true);
    //     }

    // }
}

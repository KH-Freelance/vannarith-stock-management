package com.hfsolution.feature.stockmanagement.service.report;

import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.ProductHistoryDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseItemDao;
import com.hfsolution.feature.stockmanagement.dao.ReturnDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.dto.report.CustomReportCustomerDto;
import com.hfsolution.feature.stockmanagement.dto.report.CustomReturnDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReportCustomerDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReportPurchase;
import com.hfsolution.feature.stockmanagement.dto.report.ReportReturnDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReportSaleDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReportStockDto;
import com.hfsolution.feature.stockmanagement.dto.report.ReturnDto;
import com.hfsolution.feature.stockmanagement.dto.report.SaleDto;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseSummaryDTO;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.ProductHistory;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.entity.Return;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import static com.hfsolution.app.constant.AppConstant.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
@Service
@RequiredArgsConstructor
public class ReportServiceImp  implements ReportService{
    
    private final StockDao stockDao;
    private final ProductDao productDao;
    private final PurchaseDao purchaseDao;
    private final CustomerDao customerDao;
    private final PurchaseItemDao purchaseItemDao;
    private final ReturnDao returnDao;
    private final ProductHistoryDao productHistoryDao;

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    


    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public ResponseEntity<Void> excelReportStock(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT STOCK EXCEL");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("stock-report", "UTF-8").replaceAll("\\+", "%20");
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");
            SuccessResponse<List<ReportStockDto>> result = (SuccessResponse<List<ReportStockDto>>) this.reportStock(startDate, endDate);
            EasyExcel.write(httpServletResponse.getOutputStream(), ReportStockDto.class)
            .registerWriteHandler(AppTools.createCustomStyle())
            .sheet("stock-report").doWrite(result.getData());
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public ResponseEntity<Void> excelReportCustomer(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT CUSTOMER EXCEL");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("customer-report", "UTF-8").replaceAll("\\+", "%20");
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");
            SuccessResponse<List<ReportCustomerDto>> result = (SuccessResponse<List<ReportCustomerDto>>) this.reportCustomer(startDate, endDate);

            List<CustomReportCustomerDto> customReportCustomerDtos = new ArrayList<>();
            result.getData().stream().forEach(customer->{
                CustomReportCustomerDto customReportCustomerDto = new CustomReportCustomerDto();
                customReportCustomerDto.setCustomerId(customer.getCustomerId());
                customReportCustomerDto.setCurrentCredit(customer.getCurrentCredit());
                customReportCustomerDto.setCustomerName(customer.getCustomerName());
                customReportCustomerDto.setTotalCredit(customer.getTotalCredit());
                customReportCustomerDto.setCreditDay1To30(customer.getCreditCategories().getCreditDay1To30());
                customReportCustomerDto.setCreditDay31To60(customer.getCreditCategories().getCreditDay31To60());
                customReportCustomerDto.setCreditDay61To90(customer.getCreditCategories().getCreditDay61To90());
                customReportCustomerDto.setCreditMoreThan90(customer.getCreditCategories().getCreditMoreThan90());
                customReportCustomerDtos.add(customReportCustomerDto);

            });
            EasyExcel.write(httpServletResponse.getOutputStream(), CustomReportCustomerDto.class)
            .registerWriteHandler(AppTools.createCustomStyle())
            .sheet("customer-report").doWrite(customReportCustomerDtos);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public ResponseEntity<Void> excelReportReturn(String startDate, String endDate, String customerName) {
        httpServletRequest.setAttribute(ACTION,"REPORT RETURN EXCEL");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("return-report", "UTF-8").replaceAll("\\+", "%20");
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");
            SuccessResponse<ReportReturnDto> result = (SuccessResponse<ReportReturnDto>) this.reportReturn(startDate, endDate, customerName);
            List<CustomReturnDto> customReportReturnDtos = new ArrayList<>();
            result.getData().getContent().stream().forEach(returnDto->{
                CustomReturnDto customReportReturnDto = new CustomReturnDto();
                customReportReturnDto.setBatchId(returnDto.getBatchId());
                customReportReturnDto.setPurchaseCode(returnDto.getPurchaseCode());
                customReportReturnDto.setProductName(returnDto.getProductName());
                customReportReturnDto.setType(returnDto.getType());
                customReportReturnDto.setReturnedAt(returnDto.getReturnedAt());
                customReportReturnDto.setReturnedBy(returnDto.getReturnedBy());
                customReportReturnDto.setCustomer(returnDto.getCustomer());
                customReportReturnDto.setReturnedToSales(returnDto.getReturnedToSales());
                customReportReturnDtos.add(customReportReturnDto);
            });
            EasyExcel.write(httpServletResponse.getOutputStream(), CustomReturnDto.class)
            .registerWriteHandler(AppTools.createCustomStyle())
            .sheet("return-report").doWrite(customReportReturnDtos);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public ResponseEntity<Void> excelReportSale(String startDate, String endDate,String productName, String customerName) {
        httpServletRequest.setAttribute(ACTION,"REPORT SALE EXCEL");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpServletResponse.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("sale-report", "UTF-8").replaceAll("\\+", "%20");
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");
            SuccessResponse<ReportSaleDto> result = (SuccessResponse<ReportSaleDto>) this.reportSale(startDate, endDate,productName,customerName);
            EasyExcel.write(httpServletResponse.getOutputStream(), SaleDto.class)
            .registerWriteHandler(AppTools.createCustomStyle())
            .sheet("sale-report").doWrite(result.getData().getContent());
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    // @Override
    // @Transactional
    // public File excelReportSaleTest(String startDate, String endDate, String productName, String customerName) {
    //     String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    //     long startTime = System.currentTimeMillis();
    //     try {
    //         // Generate temporary file for Excel
    //         File tempFile = File.createTempFile("sale-report", ".xlsx");
    //         try (OutputStream outputStream = new FileOutputStream(tempFile)) {
    //             SuccessResponse<ReportSaleDto> result = (SuccessResponse<ReportSaleDto>) this.reportSale(startDate, endDate, productName, customerName);
    //             EasyExcel.write(outputStream, SaleDto.class)
    //                 .registerWriteHandler(AppTools.createCustomStyle())
    //                 .sheet("sale-report")
    //                 .doWrite(result.getData().getContent());
    //         }
    //         return tempFile;
    //     } catch (Exception e) {
    //         throw new AppException(FAIL_CODE, e.getMessage(), InfoGenerator.generateInfo(currentMethodName, startTime), true);
    //     }
    // }





    @SuppressWarnings("unchecked")
    @Override
    @Transactional

    public ResponseEntity<Void> excelReportCombined(String startDate, String endDate, String customerName, String productName) {
        httpServletRequest.setAttribute(ACTION, "COMBINED REPORT EXCEL");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            HttpServletResponse response = httpServletResponse;
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            String fileName = URLEncoder.encode("combined-report", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx");

            // Create Excel writer
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream())
                    .registerWriteHandler(AppTools.createCustomStyle())
                    .build();

            // Run all report fetching tasks in parallel
            CompletableFuture<SuccessResponse<List<ReportStockDto>>> stockFuture = CompletableFuture.supplyAsync(() ->
                    (SuccessResponse<List<ReportStockDto>>) this.reportStock(startDate, endDate));

            CompletableFuture<SuccessResponse<ReportReturnDto>> returnFuture = CompletableFuture.supplyAsync(() ->
                    (SuccessResponse<ReportReturnDto>) this.reportReturn(startDate, endDate,customerName));

            CompletableFuture<SuccessResponse<List<ReportCustomerDto>>> customerFuture = CompletableFuture.supplyAsync(() ->
                    (SuccessResponse<List<ReportCustomerDto>>) this.reportCustomer(startDate, endDate));

            CompletableFuture<SuccessResponse<ReportSaleDto>> saleFuture = CompletableFuture.supplyAsync(() ->
                    (SuccessResponse<ReportSaleDto>) this.reportSale(startDate, endDate, productName, customerName));

            // Wait for all futures to complete and process the results
            CompletableFuture<Void> allOf = CompletableFuture.allOf(stockFuture,returnFuture, customerFuture, saleFuture);

            allOf.join(); // Block until all futures are done

            // Add Stock Report Sheet
            SuccessResponse<List<ReportStockDto>> stockResult = stockFuture.get();
            WriteSheet stockSheet = EasyExcel.writerSheet("stock-report").head(ReportStockDto.class).build();
            excelWriter.write(stockResult.getData(), stockSheet);

            // Add Return Report Sheet
            SuccessResponse<ReportReturnDto> returnResult = returnFuture.get();
            WriteSheet returnSheet = EasyExcel.writerSheet("return-report").head(ReturnDto.class).build();
            excelWriter.write(returnResult.getData().getContent(), returnSheet);

            // Add Customer Report Sheet
            SuccessResponse<List<ReportCustomerDto>> customerResult = customerFuture.get();
            List<CustomReportCustomerDto> customReportCustomerDtos = new ArrayList<>();
            customerResult.getData().stream().forEach(customer -> {
                CustomReportCustomerDto customReportCustomerDto = new CustomReportCustomerDto();
                customReportCustomerDto.setCustomerId(customer.getCustomerId());
                customReportCustomerDto.setCustomerName(customer.getCustomerName());
                customReportCustomerDto.setTotalCredit(customer.getTotalCredit());
                customReportCustomerDto.setCurrentCredit(customer.getCurrentCredit());
                customReportCustomerDto.setCreditDay1To30(customer.getCreditCategories().getCreditDay1To30());
                customReportCustomerDto.setCreditDay31To60(customer.getCreditCategories().getCreditDay31To60());
                customReportCustomerDto.setCreditDay61To90(customer.getCreditCategories().getCreditDay61To90());
                customReportCustomerDto.setCreditMoreThan90(customer.getCreditCategories().getCreditMoreThan90());
                customReportCustomerDtos.add(customReportCustomerDto);
            });
            WriteSheet customerSheet = EasyExcel.writerSheet("customer-report").head(CustomReportCustomerDto.class).build();
            excelWriter.write(customReportCustomerDtos, customerSheet);

            // Add Sale Report Sheet
            SuccessResponse<ReportSaleDto> saleResult = saleFuture.get();
            WriteSheet saleSheet = EasyExcel.writerSheet("sale-report").head(SaleDto.class).build();
            excelWriter.write(saleResult.getData().getContent(), saleSheet);

            // Finish writing
            excelWriter.finish();

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (DatabaseException e) {
            throw e;
        } catch (AppException e) {
            throw e;
        } catch (InterruptedException | ExecutionException e) {
            throw new AppException(FAIL_CODE, e.getMessage(), InfoGenerator.generateInfo(currentMethodName, startTime), true);
        } catch (Exception e) {
            throw new AppException(FAIL_CODE, e.getMessage(), InfoGenerator.generateInfo(currentMethodName, startTime), true);
        }
    }


    @Override
    @Transactional
    public Object reportStock(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT STOCK");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByDateRange(Timestamp.valueOf(startDate),Timestamp.valueOf(endDate));
            if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntityList()==null){
                String msg = AppTools.appGetMessage("024");
                throw new AppException("024",msg);
            }

            CompletableFuture<BaseEntityResponseDto<Product>> productFuture =  productDao.getAllEntityByIdAsync(stockResult.getEntityList().stream().map(stock->stock.getProductId()).toList());
            List<ReportStockDto> reportStockDtos = new ArrayList<>();
            BigDecimal totalAsset = BigDecimal.ZERO;
            BigDecimal totalRetail = BigDecimal.ZERO;
            Map<Long, Product> productMap =  new HashMap<>();
            productFuture.get().getEntityList().forEach(product -> productMap.put(product.getId(), product));
            for (Stock stock : stockResult.getEntityList()) {
               
                
                ReportStockDto reportStock = new ReportStockDto();                     
                if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntityList()==null){
                    String msg = AppTools.appGetMessage("024");
                    throw new AppException("024",msg);
                }


                Product product = productMap.get(stock.getProductId());
                reportStock.setProductId(product.getId());
                reportStock.setStockId(stock.getId());
                reportStock.setBatchId(stock.getBatchId());
                reportStock.setAssetValue(stock.getImportPrice().multiply(BigDecimal.valueOf(stock.getQty())));
                reportStock.setRetailValue(product.getPrice().multiply(BigDecimal.valueOf(stock.getQty())));
                reportStock.setProductName(product.getProductName());
                reportStock.setSalePrice(product.getPrice());
                reportStock.setAvgCost(stock.getImportPrice());
                //reportStock.setFactory(product.getFactory());
                reportStock.setInn(product.getProductDesc());
                reportStock.setStockOnHand(stock.getQty());
                reportStock.setDiscount(product.getDiscount());
                reportStock.setCreatedDate(stock.getCreatedDate());
                reportStock.setExpiryDate(stock.getExpiryDate());

                totalAsset = totalAsset.add(reportStock.getAssetValue());
                totalRetail = totalRetail.add(reportStock.getRetailValue());

                reportStockDtos.add(reportStock);
            }

            // Calculate % of Total Retail and Total Asset
            for (ReportStockDto report : reportStockDtos) {
                BigDecimal percentageOfTotalAsset = report.getAssetValue().divide(totalAsset,2, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
                BigDecimal percentageOfTotalReatail = report.getRetailValue().divide(totalRetail,2,RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
                report.setTotalAsset(percentageOfTotalAsset);
                report.setTotalRetail(percentageOfTotalReatail);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
       
    }

    @Override
    @Transactional
    public Object reportCustomer(String startDate, String endDate) {
        httpServletRequest.setAttribute(ACTION,"REPORT CUSTOMER");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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

                // Calculate Current Credit
                BigDecimal credit = BigDecimal.ZERO;
                for (Purchase purchase : customer.getPurchases()) {
                    if(purchase.getPaymentType().compareTo(PaymentType.CASH) == 0 || purchase.getPaymentStatus().compareTo(PaymentStatus.PAID) == 0) continue;
                    BigDecimal totalAoumtPaid = BigDecimal.ZERO;
                    for (Payment payment : purchase.getPayments()) {
                        totalAoumtPaid = totalAoumtPaid.add(payment.getAmount());
                    }
                    credit = credit.add(purchase.getTotal().subtract(totalAoumtPaid));
                    
                }
                cusomerReport.setCurrentCredit(credit);

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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    @Transactional
    public Object reportPurchase(String startDate, String endDate) {

        httpServletRequest.setAttribute(ACTION,"REPORT PURCHASE");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    @Transactional
    public Object reportSale(String startDate, String endDate,String productName, String customerName) {
        httpServletRequest.setAttribute(ACTION,"REPORT SALE");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            List<Purchase> purchaseResult = new ArrayList<>();
            List<SaleDto> saleDtos = new ArrayList<>();
            
            if(productName == null && customerName == null){
                purchaseResult =  purchaseDao.findPurchaseByCreatedDateBetween(startDate,endDate).getEntityList();
            }else if(productName != null){
                purchaseResult =  purchaseDao.findPurchaseByProductNameAndCreatedDateBetween(productName,startDate,endDate).getEntityList();
            }else{
                purchaseResult =  purchaseDao.findPurchaseByCustomerNameAndCreatedDateBetween(customerName,startDate,endDate).getEntityList();
            }
            
            Long totalQty =  0L;
            BigDecimal totalAmount =  BigDecimal.ZERO;
            for (Purchase purchase : purchaseResult) {
                for (com.hfsolution.feature.stockmanagement.entity.PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
                    //Filter Out PurcahseItem status = RETURN
                    if (purchaseItem.getStatus().equalsIgnoreCase("RETURN")) continue;
                    SaleDto saleDto = new SaleDto();
                  
                    Product product = purchaseItem.getProduct();
                    if(purchaseItem.getProduct()==null){
                        // Fallback to product history
                        ProductHistory productHistory = productHistoryDao.findById(purchaseItem.getProductId()).getEntity();
                        if (productHistory != null) {
                            product = new Product();
                            BeanUtils.copyProperties(productHistory, product);
                        }
                    }
                    
                    // Filter out Product That not contain Product Name that user want to search because one Purcahse have many product 
                    if(productName != null && !product.getProductName().contains(productName)) continue;

                    saleDto.setType("INVOICE");
                    saleDto.setProductName(product.getProductName());
                    saleDto.setProductDesc(product.getProductDesc());
                    saleDto.setSalePrice(product.getPrice());
                    saleDto.setTotalAmount(purchaseItem.getPrice().multiply(BigDecimal.valueOf(purchaseItem.getQty())));
                    saleDto.setPurchaseCode(purchase.getPurchaseCode());
                    saleDto.setCustomerName(purchase.getCustomer().getCustomerName());
                    saleDto.setCustomerPhone(purchase.getCustomer().getPhone());
                    saleDto.setQty(purchaseItem.getQty());  
                    saleDto.setLocation(purchase.getLocation());
                    saleDto.setCreatedDate(purchase.getCreatedDate());

                    totalQty += saleDto.getQty();
                    totalAmount = totalAmount.add(saleDto.getTotalAmount());

                    saleDtos.add(saleDto);
                }
            }


            // SET Total Record
            SaleDto saleDto = new SaleDto();
            saleDto.setType("Total");
            saleDto.setProductName("");
            saleDto.setProductDesc("");
            saleDto.setSalePrice(BigDecimal.ZERO);
            saleDto.setLocation("");
            saleDto.setCreatedDate(null);
            saleDto.setPurchaseCode("");
            saleDto.setCustomerName("");
            saleDto.setCustomerPhone("");
            saleDto.setTotalAmount(totalAmount);
            saleDto.setQty(totalQty);
            saleDtos.add(saleDto);
            
            ReportSaleDto reportSaleDto = new ReportSaleDto();
            reportSaleDto.setContent(saleDtos);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(reportSaleDto);
            return response;
            
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    @Transactional
    public Object reportReturn(String startDate, String endDate, String customerName) {
        httpServletRequest.setAttribute(ACTION,"REPORT RETURN");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            List<Return> returns = returnDao.findAllByCreatedDateBetween(startDate, endDate).getEntityList();
            List<Purchase> purchases = new ArrayList<>();
            if(customerName != null) {
                purchases = purchaseDao.findByPurchaseCodeInAndCustomerCustomerNameContaining(returns.stream().map(rt->rt.getSourcePurchaseCode()).toList(),customerName).getEntityList();
            }else{
                purchases = purchaseDao.findByPurchaseCodeIn(returns.stream().map(rt->rt.getSourcePurchaseCode()).toList()).getEntityList();
            }

            

            Map<String, Purchase> purchaseMap = purchases.stream().collect(Collectors.toMap(Purchase::getPurchaseCode, purchase -> purchase));
            List<ReturnDto> returnDtos = new ArrayList<>();
            returns.stream().forEach(rt->{
                rt.getReturnItems().stream().forEach(returnItem->{
                    if(purchaseMap.get(rt.getSourcePurchaseCode()) !=null){
                        ReturnDto returnDto = new ReturnDto();
                        StringBuilder sales = new StringBuilder();
                        sales.append(rt.getSourcePurchaseCode());
                        sales.append(":");
                        sales.append("(");
                        sales.append(rt.getReturnType());
                        sales.append(") ");
                        sales.append(returnItem.getProduct().getProductName());
                        sales.append(" - ");
                        sales.append(returnItem.getBacthId());
                        returnDto.setSales(sales.toString());
                        returnDto.setType(rt.getReturnType());
                        returnDto.setBatchId(returnItem.getBacthId());
                        returnDto.setPurchaseCode(rt.getSourcePurchaseCode());
                        returnDto.setProductName(returnItem.getProduct().getProductName());
                        returnDto.setRefundAmount(rt.getRefundAmount());
                        returnDto.setType(rt.getReturnType());
                        returnDto.setCustomer(purchaseMap.get(rt.getSourcePurchaseCode()).getCustomer().getCustomerName());
                        returnDto.setReturnedToSales(rt.getTargetPurchaseCode());
                        returnDto.setReturnedBy(rt.getUser().getFirstname()+" "+rt.getUser().getLastname());
                        returnDto.setReturnedAt(rt.getCreatedDate());
                        returnDtos.add(returnDto);
                    }
                });
            });
            ReportReturnDto reportReturnDto = new ReportReturnDto();
            reportReturnDto.setContent(returnDtos);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(reportReturnDto);
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

}

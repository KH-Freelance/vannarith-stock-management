package com.hfsolution.feature.stockmanagement.service.returns;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.PaymentDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseItemDao;
import com.hfsolution.feature.stockmanagement.dao.ReturnDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.dto.customer.CustomerDto;
import com.hfsolution.feature.stockmanagement.dto.product.ProductDto;
import com.hfsolution.feature.stockmanagement.dto.purchase.PaymentDto;
import com.hfsolution.feature.stockmanagement.dto.purchase.PurchaseDetailDto;
import com.hfsolution.feature.stockmanagement.dto.purchase.PurchaseDto;
import com.hfsolution.feature.stockmanagement.dto.purchase.PurchaseItemDto;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnCashRequest;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnSaleRequest;
import com.hfsolution.feature.stockmanagement.dto.request.returns.PurchaseDetail;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnSaleRequest.SourcePurchase;
import com.hfsolution.feature.stockmanagement.dto.returns.ReturnDetailDto;
import com.hfsolution.feature.stockmanagement.dto.returns.ReturnDto;
import com.hfsolution.feature.stockmanagement.dto.returns.ReturnItemDto;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;

import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.entity.Return;
import com.hfsolution.feature.stockmanagement.entity.ReturnItem;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// import jakarta.transaction.Transactional;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class ReturnServicelmp implements ReturnService {

    private final PurchaseDao purchaseDao;
    private final PurchaseItemDao purchaseItemDao;
    private final StockDao stockDao;
    private final HttpServletRequest httpServletRequest;
    private final CustomerDao customerDao;
    private final PaymentDao paymentDao;
    private final ReturnDao returnDao;
    private final String CASH = "CASH";
    private final String SALE = "SALE";
    private final UserRepository userRepository;

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH RETURN");
        SuccessResponse<Page<Return>> response = new SuccessResponse<>();
        try {

            Specification<Return> returns = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Return> returnResult = returnDao.searchReturn(returns,pageable);
            if(!returnResult.getStatus().equals(SUCCESS) || returnResult.getPage()==null){
                String msg = AppTools.appGetMessage("050");
                throw new AppException("050",msg);
            }

            // Page<Return> returnPage = returnResult.getPage().map(returnData ->{

            //     //Check produt for purchase item
            //     returnData.getReturnItems().stream().forEach((data->{
            //         if(data.getProduct()==null){
            //             // Fallback to product history
            //             ProductHistory productHistory = productHistoryDao.findByProductId(data.getProductId()).getEntity();
            //             if (productHistory != null) {
            //                 Product product = new Product();
            //                 BeanUtils.copyProperties(productHistory, product);
            //                 data.setProduct(product);
            //             }
            //         }
            //     }));
               
            //     return returnData;
            // });

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(returnResult.getPage());
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
    public Object searchV2(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {
        httpServletRequest.setAttribute(ACTION,"SEARCH RETURN");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            Specification<Return> returns = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Return> returnResult = returnDao.searchReturn(returns,pageable);
            if(!returnResult.getStatus().equals(SUCCESS) || returnResult.getPage()==null){
                String msg = AppTools.appGetMessage("062");
                throw new AppException("062",msg);
            }

            Page<ReturnDto> purchaseDtoPage = returnResult.getPage().map(returnData ->{
                ReturnDto returnDto = new ReturnDto();
                returnDto.setTotal(returnData.calculateTotalPrice());
                returnDto.setQty(returnData.calculateTotalQty());
                BeanUtils.copyProperties(returnData, returnDto);
                // //Check produt for purchase item
                // returnData.getReturnItems().stream().forEach((data->{
                //     if(data.getProduct()==null){
                //         // Fallback to product history
                //         ProductHistory productHistory = productHistoryDao.findById(data.getProductId()).getEntity();
                //         if (productHistory != null) {
                //             Product product = new Product();
                //             BeanUtils.copyProperties(productHistory, product);
                //             data.setProduct(product);
                //         }
                //     }
                // }));
                return returnDto;
            });

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(purchaseDtoPage);
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
    public Object searchDetail(Long id) {
        httpServletRequest.setAttribute(ACTION,"SEARCH RETURN DETAIL BY ID");
        SuccessResponse<Object> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            BaseEntityResponseDto<Return> returnResult = returnDao.findById(id);
            if(!returnResult.getStatus().equals(SUCCESS) || returnResult.getEntity()==null){
                String msg = AppTools.appGetMessage("062");
                throw new AppException("062",msg);
            }

            Return returns = returnResult.getEntity();

            ReturnDetailDto returnDetailDto = new ReturnDetailDto();
            BeanUtils.copyProperties(returns, returnDetailDto);

            com.hfsolution.feature.stockmanagement.dto.user.User userDto = new com.hfsolution.feature.stockmanagement.dto.user.User();
            BeanUtils.copyProperties(returns.getUser(), userDto);

            List<ReturnItemDto> returnItemDtos = new ArrayList<>();
            for (ReturnItem returnItem : returns.getReturnItems()) {
                Product product = returnItem.getProduct();
                // if(product==null){
                //     // Fallback to product history
                //     ProductHistory productHistory = productHistoryDao.findById(returnItem.getProductId()).getEntity();
                //     if (productHistory != null) {
                //         product = new Product();
                //         BeanUtils.copyProperties(productHistory, product);
                //     }
                // }
                ReturnItemDto returnItemDto = new ReturnItemDto();
                BeanUtils.copyProperties(returnItem, returnItemDto);
                ProductDto productDto = new ProductDto();
                BeanUtils.copyProperties(product, productDto);
                returnItemDto.setProduct(productDto);
                returnItemDtos.add(returnItemDto);
            }

            
            returnDetailDto.setReturnItemDtos(returnItemDtos);
            returnDetailDto.setUser(userDto);
            

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(returnDetailDto);
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
    public Object returnSale(ReturnSaleRequest returnRequest) {

        httpServletRequest.setAttribute(ACTION,"RETURN SALE");
        SuccessResponse<Purchase> response = new SuccessResponse<>();
        try {
            Long userId = (Long) Optional.ofNullable(httpServletRequest.getAttribute(USERID)).orElseThrow(() -> new AppException("002", "User ID is null"));
            Optional<User> userResult = userRepository.findById(userId);
            if(!userResult.isPresent()){
                String msg = AppTools.appGetMessage("002");
                throw new AppException("002",msg);
            }
            User user = userResult.get();
            String sourcePurchaseCode = returnRequest.getSourcePurchase().getPurchaseCode();
            String targetPurchaseCode = returnRequest.getTargetPurchaseCode();

            //GET SOURCE PURCHASE CODE 
            BaseEntityResponseDto<Purchase> sourcePurchaseResult = purchaseDao.findByPurchaseCode(sourcePurchaseCode);
            if(!sourcePurchaseResult.getStatus().equals(SUCCESS) || sourcePurchaseResult.getEntity()==null){
                String msg = AppTools.appGetMessage("048").replace("[code]",sourcePurchaseCode);
                throw new AppException("048",msg,"Y");
            }
            Purchase sourcePurchase = sourcePurchaseResult.getEntity();
            //CHECK PAYMENT sources PURCHASE
            if(sourcePurchase.getPaymentStatus().compareTo(PaymentStatus.PAID) != 0){
                String msg = AppTools.appGetMessage("061").replace("[code]",sourcePurchaseCode);
                throw new AppException("061",msg,"Y");
            }

            //GET TARGET PURCHASE CODE 
            BaseEntityResponseDto<Purchase> targetPurchaseResult = purchaseDao.findByPurchaseCode(targetPurchaseCode);
            if(!targetPurchaseResult.getStatus().equals(SUCCESS) || targetPurchaseResult.getEntity()==null){
                String msg = AppTools.appGetMessage("048").replace("[code]",targetPurchaseCode);
                throw new AppException("048",msg,"Y");
            }
            Purchase targetPurchase = targetPurchaseResult.getEntity();

            //CHECK PAYMENT TARGET PURCHASE
            if(targetPurchase.getPaymentStatus().compareTo(PaymentStatus.PAID) == 0){
                String msg = AppTools.appGetMessage("049").replace("[code]",targetPurchaseCode);
                throw new AppException("049",msg,"Y");
            }

            //CALCULATE TOTAL AMOUNT & QTY OF RETURN ITEM
            Map<String, Stock> stockMap = new HashMap<>();
            BigDecimal totalSourceItemAmt = BigDecimal.ZERO;
            Long totalQty = 0l;
            for (String batchId : returnRequest.getSourcePurchase().getBatchId()) {


                BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByBatchId(batchId);
                if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("058").replace("[batch_id]",batchId);
                    throw new AppException("058",msg,"Y");
                }
                Stock stock = stockResult.getEntity();
                Long productId = stock.getProductId();
                stockMap.put(batchId, stock);
                
                PurchaseItem sourceItem = sourcePurchase.getPurchaseItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException("Item not found for product : " + stock.getProduct().getProductName()));
                String itemStatus = sourceItem.getStatus();
                if(itemStatus==null || itemStatus.isBlank() || itemStatus.isEmpty() || itemStatus.equalsIgnoreCase("RETURN") ){
                    String msg = AppTools.appGetMessage("064").replace("[product]",sourceItem.getProduct().getProductName());
                    throw new AppException("064",msg,"Y");
                }
                BigDecimal price = sourceItem.getPrice();
                totalSourceItemAmt = totalSourceItemAmt.add(price);
                totalQty = sourceItem.getQty()+totalQty;

            }

            //ADD INTO RETURN AND RETURN ITEM 
            Return returns = new Return();
            returns.setId(returnDao.getReturnId());
            returns.setSourcePurchaseCode(sourcePurchaseCode);
            returns.setTargetPurchaseCode(targetPurchaseCode);
            returns.setReturnType(SALE);
            returns.setUser(user);
            
            for (String batchId : returnRequest.getSourcePurchase().getBatchId()) {


                Stock stock = stockMap.get(batchId);
                Long productId = stock.getProductId();

                ReturnItem returnItem = new ReturnItem();
                PurchaseItem sourceItem = sourcePurchase.getPurchaseItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException("Item not found for product ID: " + productId));

                //UPDATE STOCK WITH SOURCE ITEM
                stock.setQty(stock.getQty()+sourceItem.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                stockDao.saveEntityAsync(stock);

                //CONTINUE ADD INTO RETURN AND RETURN ITEM 
                returnItem.setPrice(sourceItem.getPrice());
                returnItem.setQty(sourceItem.getQty());
                //returnItem.setProduct(sourceItem.getProduct());
                returnItem.setProductId(productId);
                returnItem.setBacthId(batchId);
                returns.addReturnItem(returnItem);

                //UPDATE PURCHASE ITEM STATUS
                sourceItem.setStatus("RETURN");
                purchaseItemDao.saveEntityAsync(sourceItem);

                
            }

            //MINUS SOURCE PAYMENT
            // Payment sourcePayment = new Payment();
            // sourcePayment.setId(paymentDao.getPaymentId());
            // sourcePayment.setPurchase(sourcePurchase);
            // sourcePayment.setAmount(totalSourceItemAmt.negate());
            // sourcePayment.setPaymentMethod("RETURN");
            // paymentDao.saveEntityAsync(sourcePayment);


            //TOTAL = OLD PAYMENT + NEW PAYMENT 
            BigDecimal total = totalSourceItemAmt;
            for (Payment targetPurchasePayment : targetPurchase.getPayments()) {
                if (targetPurchasePayment.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    total = total.subtract(targetPurchasePayment.getAmount());
                }else{
                    total = total.add(targetPurchasePayment.getAmount());
                }
            } 

            //IF TOTAL = TARGET PURCHSE AMOUNT
            if(targetPurchase.getTotal().compareTo(total) == 0){
                targetPurchase.setPaymentStatus(PaymentStatus.PAID);
                purchaseDao.saveEntityAsync(targetPurchase);
            }

            // IF TOTAL > TARGET PURCHAE AMOUNT 
            // CALCULATE REFUND AMOUNT 
            // CALCULATE AMOUNT FOR FINAL PAID AND CHANGE PURCHASE STATUS
            BigDecimal refundAmt = BigDecimal.ZERO;
            if(total.compareTo(targetPurchase.getTotal()) > 0){ 
                refundAmt = total.subtract(targetPurchase.getTotal());       
                totalSourceItemAmt = totalSourceItemAmt.subtract(refundAmt); 
                targetPurchase.setPaymentStatus(PaymentStatus.PAID); 
                purchaseDao.saveEntityAsync(targetPurchase); 
            } 


            //MAKE TARGET PAYMENT
            Payment targetPayment = new Payment();
            targetPayment.setId(paymentDao.getPaymentId());
            targetPayment.setPurchase(targetPurchase);
            targetPayment.setAmount(totalSourceItemAmt);
            targetPayment.setPaymentMethod("RETURN");
            paymentDao.saveEntity(targetPayment);

            //SAVE RETURN
            returns.setRefundAmount(refundAmt);
            returnDao.saveEntity(returns);


            DecimalFormat formatAmt =  new DecimalFormat("#,##0.00");
            String code = "060";
            String msg = AppTools.appGetMessage(code)
            .replace("[amount]", formatAmt.format(totalSourceItemAmt))
            .replace("[source_code]", sourcePurchaseCode)
            .replace("[target_code]", targetPurchaseCode);

            if(refundAmt.compareTo(BigDecimal.ZERO)>0){
                code = "059";
                msg = AppTools.appGetMessage(code)
                .replace("[refund_amount]",formatAmt.format(refundAmt))
                .replace("[amount]", formatAmt.format(totalSourceItemAmt))
                .replace("[source_code]", sourcePurchaseCode)
                .replace("[target_code]", targetPurchaseCode);
            }

            response.setStatus(SUCCESS);
            response.setCode(code);
            response.setMsg(msg);
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
    public Object returnCash(ReturnCashRequest returnCashRequest) {

        httpServletRequest.setAttribute(ACTION,"RETURN CASH");
        SuccessResponse<Purchase> response = new SuccessResponse<>();
        try {
            Long userId = (Long) Optional.ofNullable(httpServletRequest.getAttribute(USERID)).orElseThrow(() -> new AppException("002", "User ID is null"));
            Optional<User> userResult = userRepository.findById(userId);
            if(!userResult.isPresent()){
                String msg = AppTools.appGetMessage("002");
                throw new AppException("002",msg);
            }
            User user = userResult.get();
            //GET PURCHASE CODE 
            BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.findByPurchaseCode(returnCashRequest.getPurchaseCode());
            if(!purchaseResult.getStatus().equals(SUCCESS) || purchaseResult.getEntity()==null){
                String msg = AppTools.appGetMessage("048").replace("[code]",returnCashRequest.getPurchaseCode());
                throw new AppException("048",msg,"Y");
            }
            Purchase purchaseInfo = purchaseResult.getEntity();

            //CALCULATE TOTAL AMOUNT & QTY OF RETURN ITEM
            Map<String, Stock> stockMap = new HashMap<>();
            BigDecimal refundAmount = BigDecimal.ZERO;
            Long totalQty = 0l;
            for (String batchId : returnCashRequest.getBatchId()) {

                BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByBatchId(batchId);
                if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("058").replace("[batch_id]",batchId);
                    throw new AppException("058",msg,"Y");
                }
                Stock stock = stockResult.getEntity();
                Long productId = stock.getProductId();
                stockMap.put(batchId, stock);

                PurchaseItem itemInfo = purchaseInfo.getPurchaseItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException("Item not found for product: " +  stock.getProduct().getProductName()));
                String itemStatus = itemInfo.getStatus();
                if(itemStatus==null || itemStatus.isBlank() || itemStatus.isEmpty() || itemStatus.equalsIgnoreCase("RETURN") ){
                    String msg = AppTools.appGetMessage("064").replace("[product]",itemInfo.getProduct().getProductName());
                    throw new AppException("064",msg,"Y");
                }
                BigDecimal price = itemInfo.getPrice();
                refundAmount = refundAmount.add(price);
                totalQty = itemInfo.getQty()+totalQty;


            }

            //ADD INTO RETURN AND RETURN ITEM 
            Return returns = new Return();
            returns.setId(returnDao.getReturnId());
            returns.setSourcePurchaseCode(returnCashRequest.getPurchaseCode());
            returns.setTargetPurchaseCode("N/A");
            returns.setReturnType(CASH);
            returns.setUser(user);
            
            for (String batchId : returnCashRequest.getBatchId()) {

                Stock stock = stockMap.get(batchId);
                Long productId = stock.getProductId();

                ReturnItem returnItem = new ReturnItem();
                PurchaseItem purchaseItems = purchaseInfo.getPurchaseItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException("Item not found for product: " +  stock.getProduct().getProductName()));

                //UPDATE STOCK
                stock.setQty(stock.getQty()+purchaseItems.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                stockDao.saveEntityAsync(stock);

                //CONTINUE ADD INTO RETURN AND RETURN ITEM 
                returnItem.setPrice(purchaseItems.getPrice());
                returnItem.setQty(purchaseItems.getQty());
                //returnItem.setProduct(purchaseItems.getProduct());
                returnItem.setProductId(productId);
                returnItem.setBacthId(batchId);
                returns.addReturnItem(returnItem);

                //UPDATE PURCHASE ITEM STATUS
                purchaseItems.setStatus("RETURN");
                purchaseItemDao.saveEntityAsync(purchaseItems);

                
            }

            
            //MINUS PAYMENT
            if(!purchaseInfo.getPaymentStatus().equals(PaymentStatus.PAID)){
                
                BigDecimal remainPayment = purchaseInfo.getTotal();
                for (Payment paymentData : purchaseInfo.getPayments()) {
                    remainPayment = paymentData.getAmount().compareTo(BigDecimal.ZERO) > 0 
                    ? remainPayment.subtract(paymentData.getAmount()) 
                    : remainPayment.add(paymentData.getAmount());
                }
                BigDecimal paymentAmount = refundAmount;
                if(refundAmount.compareTo(remainPayment)>0){
                    refundAmount = refundAmount.subtract(remainPayment);
                    paymentAmount = remainPayment;
                }
                if(paymentAmount.compareTo(BigDecimal.ZERO)!=0){
                    Payment payment = new Payment();
                    payment.setAmount(paymentAmount.negate());
                    payment.setId(paymentDao.getPaymentId());
                    payment.setPurchase(purchaseInfo);
                    payment.setPaymentMethod("RETURN");
                    paymentDao.saveEntityAsync(payment);
                }
                
            }


            //SAVE RETURN
            returns.setRefundAmount(refundAmount);
            returnDao.saveEntity(returns);

            response.setStatus(SUCCESS);
            response.setCode("053");
            response.setMsg(AppTools.appGetMessage("053")
            .replace("[refund_amount]",new DecimalFormat("#,##0.00").format(refundAmount))
            .replace("[purchase_code]",returnCashRequest.getPurchaseCode()));
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

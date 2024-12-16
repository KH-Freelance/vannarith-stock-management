package com.hfsolution.feature.stockmanagement.service.returns;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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
import com.hfsolution.feature.stockmanagement.dao.PaymentDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.ProductHistoryDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseItemDao;
import com.hfsolution.feature.stockmanagement.dao.ReturnDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.dto.purchase.PurchaseDto;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnRequest;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.ProductHistory;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.entity.Return;
import com.hfsolution.feature.stockmanagement.entity.ReturnItem;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final ProductHistoryDao productHistoryDao;

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
    public Object searchDetail(long id) {
        httpServletRequest.setAttribute(ACTION,"SEARCH RETURN PURCHASE DETAIL BY ID");
        SuccessResponse<Object> response = new SuccessResponse<>();
        try {
            BaseEntityResponseDto<Return> returnResult = returnDao.findById(id);
            if(!returnResult.getStatus().equals(SUCCESS) || returnResult.getEntity()==null){
                String msg = AppTools.appGetMessage("050");
                throw new AppException("050",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(returnResult.getEntity());
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
    public Object returnPurchase(ReturnRequest returnRequest) {

        httpServletRequest.setAttribute(ACTION,"RETURN PURCHASE");
        SuccessResponse<Purchase> response = new SuccessResponse<>();
        try {
            
            String sourcePurchaseCode = returnRequest.getSourcePurchase().getPurchaseCode();
            String targetPurchaseCode = returnRequest.getTargetPurchaseCode();

            //GET SOURCE PURCHASE CODE 
            BaseEntityResponseDto<Purchase> sourcePurchaseResult = purchaseDao.findByPurchaseCode(sourcePurchaseCode);
            if(!sourcePurchaseResult.getStatus().equals(SUCCESS) || sourcePurchaseResult.getEntity()==null){
                String msg = AppTools.appGetMessage("048").replace("[code]",sourcePurchaseCode);
                throw new AppException("048",msg);
            }
            Purchase sourcePurchase = sourcePurchaseResult.getEntity();

            //GET TARGET PURCHASE CODE 
            BaseEntityResponseDto<Purchase> targetPurchaseResult = purchaseDao.findByPurchaseCode(targetPurchaseCode);
            if(!targetPurchaseResult.getStatus().equals(SUCCESS) || targetPurchaseResult.getEntity()==null){
                String msg = AppTools.appGetMessage("048").replace("[code]",sourcePurchaseCode);
                throw new AppException("048",msg,"Y");
            }
            Purchase targetPurchase = targetPurchaseResult.getEntity();

            //CHECK PAYMENT TARGET PURCHASE
            if(targetPurchase.getPaymentStatus().compareTo(PaymentStatus.PAID) == 0){
                String msg = AppTools.appGetMessage("049").replace("[code]",targetPurchaseCode);
                throw new AppException("049",msg,"Y");
            }

            //CALCULATE TOTAL AMOUNT & QTY OF RETURN ITEM
            BigDecimal totalSourceItemAmt = BigDecimal.ZERO;
            Long totalQty = 0l;
            for (Long productId : returnRequest.getSourcePurchase().getProductIds()) {

                PurchaseItem sourceItem = sourcePurchase.getPurchaseItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException("Item not found for product ID: " + productId));
                BigDecimal price = sourceItem.getPrice();
                totalSourceItemAmt = totalSourceItemAmt.add(price);
                totalQty = sourceItem.getQty()+totalQty;

            }

            //ADD INTO RETURN AND RETURN ITEM 
            Return returns = new Return();
            returns.setId(returnDao.getReturnId());
            returns.setPurchase(sourcePurchase);
            returns.setTotalAmount(totalSourceItemAmt);
            returns.setTotalQty(totalQty);
            
            for (Long productId : returnRequest.getSourcePurchase().getProductIds()) {

                ReturnItem returnItem = new ReturnItem();
                PurchaseItem sourceItem = sourcePurchase.getPurchaseItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new AppException("Item not found for product ID: " + productId));

                //UPDATE STOCK
                BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByProductID(productId);
                if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("046").replace("[product]",sourceItem.getProduct().getProductName());
                    throw new AppException("046",msg,"Y");
                }
                Stock stock = stockResult.getEntity();
                stock.setQty(stock.getQty()+sourceItem.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                stockDao.saveEntityAsync(stock);

                //CONTINUE ADD INTO RETURN AND RETURN ITEM 
                returnItem.setAmount(sourceItem.getPrice());
                returnItem.setQty(sourceItem.getQty());
                returnItem.setProduct(sourceItem.getProduct());
                returns.addReturnItem(returnItem);

                //UPDATE PURCHASE ITEM STATUS
                sourceItem.setStatus("RETURN");
                purchaseItemDao.saveEntityAsync(sourceItem);

                
            }

            //MINUS SOURCE PAYMENT
            Payment sourcePayment = new Payment();
            sourcePayment.setId(paymentDao.getPaymentId());
            sourcePayment.setPurchase(targetPurchase);
            sourcePayment.setAmount(totalSourceItemAmt.negate());
            paymentDao.saveEntity(sourcePayment);

            //TOTAL = OLD PAYMENT + NEW PAYMENT 
            BigDecimal total = totalSourceItemAmt;
            for (Payment targetPurchasePayment : targetPurchase.getPayments()) {
                total = total.add(targetPurchasePayment.getAmount());
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

            //SAVE RETURN
            returns.setRefundAmount(refundAmt);
            returnDao.saveEntity(returns);

            //MAKE TARGET PAYMENT
            Payment targetPayment = new Payment();
            targetPayment.setId(paymentDao.getPaymentId());
            targetPayment.setPurchase(targetPurchase);
            targetPayment.setAmount(totalSourceItemAmt);
            paymentDao.saveEntity(targetPayment);

            response.setStatus(SUCCESS);
            response.setCode("051");
            response.setMsg(AppTools.appGetMessage("051")
            .replace("[source_code]", sourcePurchaseCode)
            .replace("[target_code]", targetPurchaseCode));
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

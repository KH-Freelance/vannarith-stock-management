package com.hfsolution.feature.stockmanagement.service.returns;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseItemDao;
import com.hfsolution.feature.stockmanagement.dao.ReturnDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dao.StockHistoryDao;
import com.hfsolution.feature.stockmanagement.dto.request.returns.ReturnRequest;
import com.hfsolution.feature.stockmanagement.entity.Payment;
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
import jakarta.transaction.Transactional;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class ReturnServicelmp implements ReturnService {

    private final PurchaseDao purchaseDao;
    private final PurchaseItemDao purchaseItemDao;
    private final StockDao stockDao;
    private final HttpServletRequest httpServletRequest;
    private final PaymentDao paymentDao;
    private final ReturnDao returnDao;

    @Override
    @Transactional
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
    public Object returnPurchase(ReturnRequest returnRequest) {

        httpServletRequest.setAttribute(ACTION,"IMPORT STOCK");
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
                .findFirst().get();
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
                .findFirst().get();

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

                returnItem.setAmount(sourceItem.getPrice());
                returnItem.setQty(sourceItem.getQty());
                returnItem.setProduct(sourceItem.getProduct());
                returns.addReturnItem(returnItem);
                

                //UPDATE PURCHASE ITEM STATUS
                sourceItem.setStatus("RETURN");
                purchaseItemDao.saveEntityAsync(sourceItem);
            }

            //SAVE RETURN
            returnDao.saveEntityAsync(returns);

            //UPDATE PURCHASE SUBTRACT RETURN PRODUCT PRICE
            // sourcePurchase.setTotal(sourcePurchase.getTotal().subtract(totalSourceItemAmt));
            // purchaseDao.saveEntityAsync(sourcePurchase);

            //TOTAL OLD PAY WITH NEW PAY
            BigDecimal total = totalSourceItemAmt;
            for (Payment targetPurchasePayment : targetPurchase.getPayments()) {
                total = total.add(targetPurchasePayment.getAmount());
            } 

            //IF TOTAL = TARGET PURCHSE AMOUNT
            if(targetPurchase.getTotal().compareTo(total) == 0){
                targetPurchase.setPaymentStatus(PaymentStatus.PAID);
                purchaseDao.saveEntity(targetPurchase);
            }

            // IF TOTAL > TARGET PURCHAE AMOUNT 
            // CALCULATE REFUND AMOUNT 
            // CALCULATE AMOUNT FOR FINAL PAID AND CHANGE PURCHASE STATUS
            BigDecimal refundAmt = BigDecimal.ZERO;
            if(total.compareTo(targetPurchase.getTotal()) > 0){
                refundAmt = total.subtract(targetPurchase.getTotal());       
                totalSourceItemAmt = totalSourceItemAmt.subtract(refundAmt); 
                targetPurchase.setPaymentStatus(PaymentStatus.PAID);
                purchaseDao.saveEntity(targetPurchase);
            }

            //MAKE PAYMENT
            Payment payment = new Payment();
            payment.setId(paymentDao.getPaymentId());
            payment.setPurchase(targetPurchase);
            payment.setAmount(totalSourceItemAmt);
            paymentDao.saveEntity(payment);

            //# HOLD IT IN DISCUSS
            //IF CUSTOMER CREDIT != 0 ADD CREDIT WITH REMAIN AMT
            // Customer customer = targetPurchase.getCustomer();
            // if(customer.getCredit().compareTo(BigDecimal.ZERO)!=0){
            //     customer.setCredit(customer.getCredit().subtract(totalSourceItemAmt));
            //     customerDao.saveEntity(customer);
            
            // //ELSE REFUND TO CUSTOMER BY REAL MONEY
            // }else{

            //     System.out.println("============== REFUND : "+refundAmt);
            // }
            
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

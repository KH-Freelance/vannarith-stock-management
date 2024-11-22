package com.hfsolution.feature.stockmanagement.service.purchase;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;

import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;

import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.CSVHelper;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.PaymentDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dto.CsvRepresentation.PurchaseCsv;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PayRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseRequest.ProductPurchase;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.enums.PaymentType;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.enums.Role;
import com.hfsolution.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// import jakarta.transaction.Transactional;

import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class PurchaseServicelmp implements PurchaseService {

    private final PurchaseDao purchaseDao;
    private final StockDao stockDao;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final UserRepository userRepository;
    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final PaymentDao paymentDao;
    private final String CSV_FILENAME="purchase";

    @Override
    @Transactional
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH PURCHASE");
        SuccessResponse<Page<Purchase>> response = new SuccessResponse<>();
        try {
            Map<String, Class<? extends Enum>> enumFields = new HashMap<>();
            enumFields.put("paymentType", PaymentType.class); 
            enumFields.put("paymentStatus", PaymentStatus.class); 
            Specification<Purchase> purchases = new CustomSpecification<>(q,enumFields);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
            BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.searchPurchase(purchases,pageable);
            if(!purchaseResult.getStatus().equals(SUCCESS) || purchaseResult.getPage()==null){
                String msg = AppTools.appGetMessage("032");
                throw new AppException("032",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(purchaseResult.getPage());
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
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT PURCHASE");
        try {
            CSVHelper<PurchaseCsv> csvService = new CSVHelper<>(PurchaseCsv.class,httpServletResponse);
            Specification<Purchase> purchases = new CustomSpecification<>(q);
            BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.searchPurchase(purchases);
            List<PurchaseCsv> purchaseCsvs = new ArrayList<>();
            purchaseResult.getEntityList().stream().forEach(purchase -> {
                PurchaseCsv purchaseCsv = new PurchaseCsv();
                purchaseCsv.setId(purchase.getId());
                purchaseCsv.setProductName(purchase.getProduct().getProductName());
                purchaseCsv.setProductId(purchase.getProduct().getId());
                purchaseCsv.setCustomerName(purchase.getCustomer().getCustomerName());
                purchaseCsv.setCustomerId(purchase.getCustomer().getId());
                purchaseCsv.setEmployeeName(purchase.getUser().getFirstname()+" "+purchase.getUser().getLastname());
                purchaseCsv.setEmployeeId(purchase.getUser().getId());
                purchaseCsv.setQty(purchase.getQty());
                purchaseCsv.setTotal(purchase.getTotal());
                purchaseCsv.setPaymentStatus(purchase.getPaymentStatus());
                purchaseCsv.setPaymentType(purchase.getPaymentType());
                purchaseCsv.setLocation(purchase.getLocation());
                purchaseCsv.setDiscount(purchase.getDiscount());
                purchaseCsv.setCreatedDate(purchase.getCreatedDate());
                purchaseCsv.setUpdateDate(purchase.getUpdatedDate());
                purchaseCsvs.add(purchaseCsv);
            });
            csvService.export(purchaseCsvs, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("036",e.getMessage(),true);  
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT PURCHASE");
        try {
            CSVHelper<PurchaseCsv> csvService = new CSVHelper<>(PurchaseCsv.class);
            List<PurchaseCsv> purchaseCSVList = csvService.parseCsv(file);
            List<Purchase> purchaseList = new ArrayList<>();
            purchaseCSVList.stream().forEach(purchaseCsv -> {

                BaseEntityResponseDto<Product> productResult = productDao.findById(purchaseCsv.getProductId());
                if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("006");
                    throw new AppException("006",msg);
                }

                BaseEntityResponseDto<Customer> customerResult = customerDao.findById(purchaseCsv.getCustomerId());
                if(!customerResult.getStatus().equals(SUCCESS) || customerResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("015");
                    throw new AppException("015",msg);
                }

                Purchase purchase = new Purchase();
                purchase.setProduct(productResult.getEntity());
                purchase.setCustomer(customerResult.getEntity());
                purchase.setUser(userRepository.findById(purchaseCsv.getEmployeeId()).orElseThrow(() -> new AppException("002", "User not found")));
                purchase.setQty(purchaseCsv.getQty());
                purchase.setDiscount(purchaseCsv.getDiscount());
                purchase.setTotal(purchaseCsv.getTotal());
                purchase.setLocation(purchaseCsv.getLocation());
                purchase.setPaymentType(purchaseCsv.getPaymentType());
                purchase.setPaymentStatus(purchaseCsv.getPaymentStatus());
                purchase.setCreatedDate(purchaseCsv.getCreatedDate());
                purchase.setUpdatedDate(purchaseCsv.getUpdateDate());
                purchase.setId(purchaseCsv.getId());
                purchaseList.add(purchase);
            });         
            purchaseDao.saveEntities(purchaseList);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("039"));
            response.setCode("039");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("038",e.getMessage(),true);  
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    
    }
    


    @Override
    public Object addPurchase(PurchaseRequest purchaseRequest) {

        httpServletRequest.setAttribute(ACTION,"IMPORT STOCK");
        SuccessResponse<Purchase> response = new SuccessResponse<>();
        try {

            //GET USER 
            Long userId = (Long) Optional.ofNullable(httpServletRequest.getAttribute(USERID)).orElseThrow(() -> new AppException("002", "User ID is null"));
            Optional<User> userResult = userRepository.findById(userId);
            if(!userResult.isPresent()){
                String msg = AppTools.appGetMessage("002");
                throw new AppException("002",msg);
            }
            User user = userResult.get();

            //GET CUSTOMER 
            BaseEntityResponseDto<Customer> customerResult = customerDao.findById(purchaseRequest.getCustomerId());
            if(!customerResult.getStatus().equals(SUCCESS) || customerResult.getEntity()==null){
                String msg = AppTools.appGetMessage("015");
                throw new AppException("015",msg);
            }
            Customer customer = customerResult.getEntity();

            //VERIFY PRODUCT & STOCK
            Map<Long, Product> productMap = new HashMap<>();
            Map<Long, Stock> stockMap = new HashMap<>();
            purchaseRequest.getProductPurchases().stream().forEach((data->{

                //CHECK PRODUCT
                BaseEntityResponseDto<Product> productResult = productDao.findById(data.getProductId());
                if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("006");
                    throw new AppException("006",msg);
                }
                Product product = productResult.getEntity();
                productMap.put(data.getProductId(), product);

                //CHECK STOCK & QTY
                BaseEntityResponseDto<Stock> stockResult = stockDao.findStockByProductID(data.getProductId());
                if(!stockResult.getStatus().equals(SUCCESS) || stockResult.getEntity()==null){
                    String msg = AppTools.appGetMessage("046").replace("[product]",product.getProductName());;
                    throw new AppException("046",msg,"Y");
                }
                Stock stock = stockResult.getEntity();
                stockMap.put(data.getProductId(), stock);
                if(stock.getQty() == null || stock.getQty() <= 0){
                    String msg = AppTools.appGetMessage("047").replace("[product]",product.getProductName());;
                    throw new AppException("047",msg,"Y");
                }
                if(stock.getQty() < data.getQty()){
                    String msg = AppTools.appGetMessage("047").replace("[product]",product.getProductName());;
                    throw new AppException("047",msg,"Y");
                }
                

            }));
            
            //PROCESS PURCHASE
            for (ProductPurchase productPurchase : purchaseRequest.getProductPurchases()){

                Product product = productMap.get(productPurchase.getProductId());
                Stock stock = stockMap.get(productPurchase.getProductId());

                //CALCULATE
                BigDecimal basePrice = product.getPrice().multiply(BigDecimal.valueOf(productPurchase.getQty()));
                BigDecimal totalDiscount = Optional.ofNullable(product.getDiscount()).orElse(BigDecimal.ZERO)
                                    .add(Optional.ofNullable(customer.getDiscount()).orElse(BigDecimal.ZERO))
                                    .add(Optional.ofNullable(purchaseRequest.getDiscount()).orElse(BigDecimal.ZERO));
                BigDecimal discountPrice = basePrice.multiply(totalDiscount.divide(BigDecimal.valueOf(100)));
                BigDecimal totalPrice = basePrice.subtract(discountPrice);


                Purchase purchase = new Purchase();
                purchase.setId(purchaseDao.getPurchaseId());
                purchase.setProduct(product);
                purchase.setCustomer(customer);
                purchase.setUser(user);
                purchase.setQty(productPurchase.getQty());
                purchase.setDiscount(totalDiscount);
                purchase.setTotal(basePrice);
                purchase.setLocation(purchaseRequest.getLocation());
                purchase.setPaymentType(purchaseRequest.getPaymentType()); 
            

                // Check PaymentType return 0 = PAID, -1 = CREDIT
                if(purchaseRequest.getPaymentType().compareTo(PaymentType.CASH)==0){
                    // Status PAID
                    purchase.setPaymentStatus(PaymentStatus.PAID);
                
                }else{
                    // Status CREDIT
                    purchase.setPaymentStatus(PaymentStatus.CREDIT);
                    customer.setCredit(customer.getCredit().add(totalPrice));
                    customerDao.saveEntity(customer);
                }
                
                BaseEntityResponseDto<Purchase> pur = purchaseDao.saveEntity(purchase);
                if(pur.getEntity().getPaymentStatus().compareTo(PaymentStatus.PAID)==0){
                    // Save Payment for PAID
                    Payment payment = new Payment();
                    payment.setId(paymentDao.getPaymentId());
                    payment.setPurchase(pur.getEntity());
                    payment.setAmount(totalPrice);
                    paymentDao.saveEntity(payment);
                }
                
                // Minus from Stock
                stock.setQty(stock.getQty()-productPurchase.getQty());
                stock.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
                stockDao.saveEntity(stock);
                
            }

            response.setStatus(SUCCESS);
            response.setCode("034");
            response.setMsg(AppTools.appGetMessage("034"));
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
    public Object deletePurchaseById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE PURCHASE BY ID");
        SuccessResponse<Purchase> response = new SuccessResponse<>();
        try {
            paymentDao.deleteByPurchaseId(id);
            purchaseDao.deletePurchaseByID(id);
            String msg = AppTools.appGetMessage("033");
            response.setStatus(SUCCESS);
            response.setCode("033");
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
    public Object pay(Long id, PayRequest payRequest) {
        httpServletRequest.setAttribute(ACTION,"PAYMENT");
        SuccessResponse<Purchase> response = new SuccessResponse<>();
        try {

            // Check existing Purchase 
            BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.findById(id);
            if(!purchaseResult.getStatus().equals(SUCCESS) || purchaseResult.getEntity()==null){
                String msg = AppTools.appGetMessage("032");
                throw new AppException("032",msg);
            }

            // Check Purchase PAID or CREDIT
            Purchase purchase = purchaseResult.getEntity();
            if(purchase.getPaymentStatus().compareTo(PaymentStatus.PAID) == 0){
                String msg = AppTools.appGetMessage("040");
                throw new AppException("040",msg);
            }

            // Check if the amount over
            BigDecimal total = payRequest.getAmount();
            for (Payment payment : purchase.getPayments()) {
                total = total.add(payment.getAmount());
            }

            if(purchase.getTotal().compareTo(total) < 0){
                String msg = AppTools.appGetMessage("044");
                throw new AppException("044",msg);
            }

            // Create Payment
            Payment payment = new Payment();
            payment.setId(paymentDao.getPaymentId());
            payment.setPurchase(purchase);
            payment.setAmount(payRequest.getAmount());
            paymentDao.saveEntity(payment);

            // Update Purchase Status to PAID
            if(purchase.getTotal().compareTo(total) == 0){
                purchase.setPaymentStatus(PaymentStatus.PAID);
                purchaseDao.saveEntity(purchase);
            }

            //GET CUSTOMER 
            BaseEntityResponseDto<Customer> customerResult = customerDao.findById(purchase.getCustomer().getId());
            if(!customerResult.getStatus().equals(SUCCESS) || customerResult.getEntity()==null){
                String msg = AppTools.appGetMessage("027");
                throw new AppException("027",msg);
            }
            // Subtrac Credit of Customer
            Customer customer = customerResult.getEntity();
            customer.setCredit(customer.getCredit().subtract(payRequest.getAmount()));
            customerDao.saveEntity(customer);


            String msg = AppTools.appGetMessage("041");
            response.setStatus(SUCCESS);
            response.setCode("041");
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

    
}

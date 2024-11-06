package com.hfsolution.feature.stockmanagement.service.purchase;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.CsvException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CSVService;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.services.SearchFilter;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.PaymentDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseRequest;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseUpdateRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockRequest;
import com.hfsolution.feature.stockmanagement.dto.request.stock.StockUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class PurchaseServicelmp implements PurchaseService {

    private final PurchaseDao purchaseDao;
    private final HttpServletRequest httpServletRequest;
    private final SearchFilter<Purchase> searchFilter;
    private final UserRepository userRepository;
    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final PaymentDao paymentDao;
    private final CSVService<Purchase> csvService;
    private final String CSV_FILENAME="purchase";

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH PURCHASE");
        SuccessResponse<Page<Purchase>> response = new SuccessResponse<>();
        try {

            Specification<Purchase> purchases = new CustomSpecification<>(q);
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
            Specification<Purchase> purchases = new CustomSpecification<>(q);
            BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.searchPurchase(purchases);
            csvService.export(purchaseResult.getEntityList(),Purchase.class, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT PURCHASE");
        try {

            List<Purchase> purchaseList = csvService.parseCsv(file, Purchase.class);
            purchaseDao.saveEntities(purchaseList);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("039"));
            response.setCode("039");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (CsvException e) {
            throw new AppException("038",e.getMessage(),true); 
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        
    }
    
    }
    

    @Override
    public Object searchPurchase(SearchRequestDTO request) {


        

        httpServletRequest.setAttribute(ACTION,"SEARCH PURCHASE");
        SuccessResponse<Page<Purchase>> response = new SuccessResponse<>();
        try {

            Specification<Purchase> purchase = searchFilter.getSearchSpecification(request.getSearchRequest(), request.getGlobalOperator());
            Pageable pageable = new PageRequestDto().getPageable(request.getPageRequestDto());
            BaseEntityResponseDto<Purchase> productResult = purchaseDao.searchPurchase(purchase,pageable);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getPage()==null){
                String msg = AppTools.appGetMessage("032");
                throw new AppException("032",msg);
            }
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setData(productResult.getPage());
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
    public Object addPurchase(PurchaseRequest purchaseRequest) {

        httpServletRequest.setAttribute(ACTION,"IMPORT STOCK");
        SuccessResponse<Stock> response = new SuccessResponse<>();
        try {

            //GET USER 
            Optional<User> userResult = userRepository.findById(purchaseRequest.getUserId());
            if(!userResult.isPresent()){
                String msg = AppTools.appGetMessage("002");
                throw new AppException("002",msg);
            }
            User user = userResult.get();

            //GET PRODUCT
            BaseEntityResponseDto<Product> productResult = productDao.findById(purchaseRequest.getProductId());
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
                String msg = AppTools.appGetMessage("006");
                throw new AppException("006",msg);
            }
            Product product = productResult.getEntity();

            //GET CUSTOMER 
            BaseEntityResponseDto<Customer> customerResult = customerDao.findById(purchaseRequest.getCustomerId());
            if(!customerResult.getStatus().equals(SUCCESS) || customerResult.getEntity()==null){
                String msg = AppTools.appGetMessage("027");
                throw new AppException("027",msg);
            }
            Customer customer = customerResult.getEntity();

            //CALCULATE
            BigDecimal basePrice = product.getPrice().multiply(BigDecimal.valueOf(purchaseRequest.getQty()));
            BigDecimal totalDiscount = Optional.ofNullable(product.getDiscount()).orElse(BigDecimal.ZERO)
                                 .add(Optional.ofNullable(customer.getDiscount()).orElse(BigDecimal.ZERO));
            BigDecimal discountPrice = basePrice.multiply(totalDiscount.divide(BigDecimal.valueOf(100)));
            BigDecimal totalPrice = basePrice.subtract(discountPrice);

            Purchase purchase = new Purchase();
            purchase.setId(purchaseDao.getPurchaseId());
            purchase.setProduct(product);
            purchase.setCustomer(customer);
            purchase.setUser(user);
            purchase.setQty(purchaseRequest.getQty());
            purchase.setDiscount(totalDiscount);
            purchase.setTotal(basePrice);
            purchase.setPaymentType(purchaseRequest.getPaymentType()); 
            purchaseDao.saveEntity(purchase);

            Payment payment = new Payment();
            payment.setId(paymentDao.getPaymentId());
            payment.setPurchase(purchase);
            payment.setProduct(product);
            payment.setCustomer(customer);
            payment.setUser(user);
            payment.setAmount(totalPrice);
            paymentDao.saveEntity(payment);

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


    // @Override
    // public Object updatePurchase(Long id, PurchaseUpdateRequest purchaseUpdateRequest) {

    //     httpServletRequest.setAttribute(ACTION,"UPDATE STOCK BY ID");
    //     SuccessResponse<Stock> response = new SuccessResponse<>();
    //     try {

    //         BaseEntityResponseDto<Purchase> purchaseResult = purchaseDao.findById(id);
    //         Purchase purchase = purchaseResult.getEntity();
    //         // Optional.ofNullable(stockUpdateRequest.getProductId()).ifPresent(stock::setPro);
    //         Optional.ofNullable(stockUpdateRequest.getQty()).ifPresent(purchase::setQty);
    //         purchase.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
    //         purchaseDao.saveEntity(purchase);
    //         response.setStatus(SUCCESS);
    //         response.setCode(SUCCESS_CODE);
    //         response.setMsg(AppTools.appGetMessage("035"));
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

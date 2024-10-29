package com.hfsolution.feature.stockmanagement.service.product;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.dto.SearchRequestDTO;
import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.SearchFilter;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dto.request.ProductRequest;
import com.hfsolution.feature.stockmanagement.entity.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import static com.hfsolution.app.constant.AppConstant.*;


@Service
@RequiredArgsConstructor
public class ProductServicelmp implements ProductService {

    private final ProductDao productDao;
    private final HttpServletRequest httpServletRequest;
    private final SearchFilter<Product> searchFilter;

    @Override
    public Object search(SearchRequestDTO request) {

        httpServletRequest.setAttribute(ACTION,"SEARCH PRODUCT");
        SuccessResponse<Page<Product>> response = new SuccessResponse<>();
        try {

            Specification<Product> products = searchFilter.getSearchSpecification(request.getSearchRequest(), request.getGlobalOperator());
            Pageable pageable = new PageRequestDto().getPageable(request.getPageRequestDto());
            BaseEntityResponseDto<Product> productResult = productDao.search(products,pageable);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getPage()==null){
                String msg = AppTools.appGetMessage("006");
                throw new AppException("006",msg);
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
    public Object addProduct(ProductRequest productRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD PRODUCT");
        SuccessResponse<Product> response = new SuccessResponse<>();
        // StockManagementResponse response = new StockManagementResponse();
        try {

            Product product = new Product();
            product.setProductName(productRequest.getProductName());
            product.setProductDesc(productRequest.getProductDesc());
            product.setPrice(productRequest.getPrice());
            product.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            product.setExpiryDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(productRequest.getExpiryDate()), LocalTime.MIDNIGHT)));
            // BaseEntityResponseDto<Product> productResult = productDao.saveEntity(product);
            // if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
            //     String msg = AppTools.appGetMessage("006");
            //     throw new AppException("006",msg);
            // }
            productDao.saveEntity(product);
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg(AppTools.appGetMessage("008"));
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
    public Object deleteProductById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE PRODUCT BY ID");
        SuccessResponse<Product> response = new SuccessResponse<>();
        try {
    
            productDao.deleteByProductID(id);
            String msg = AppTools.appGetMessage("007");
            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
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
    public Object updateProductById(Long id, ProductRequest productRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE PRODUCT BY ID");
        SuccessResponse<Product> response = new SuccessResponse<>();
        try {

            //check product id
            BaseEntityResponseDto<Product> productResult = productDao.findByProductID(id);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
                String msg = AppTools.appGetMessage("006");
                throw new AppException("006",msg);
            }

            //updated
            Product existingProduct = productResult.getEntity();
            Optional.ofNullable(productRequest.getProductName()).ifPresent(existingProduct::setProductName);
            Optional.ofNullable(productRequest.getProductDesc()).ifPresent(existingProduct::setProductDesc);
            Optional.ofNullable(productRequest.getPrice()).ifPresent(existingProduct::setPrice);
            Optional.ofNullable(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(productRequest.getExpiryDate()), LocalTime.MIDNIGHT)))
            .ifPresent(existingProduct::setExpiryDate);
            existingProduct.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            productDao.saveEntity(existingProduct);

            response.setStatus(SUCCESS);
            response.setCode(SUCCESS_CODE);
            response.setMsg(AppTools.appGetMessage("009"));
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

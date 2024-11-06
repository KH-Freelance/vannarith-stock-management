package com.hfsolution.feature.stockmanagement.service.product;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
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
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dao.PurchaseDao;
import com.hfsolution.feature.stockmanagement.dao.StockDao;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.user.entity.User;
import com.hfsolution.feature.user.enums.Role;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class ProductServicelmp implements ProductService {

    private final ProductDao productDao;
    private final PurchaseDao purchaseDao;
    private final StockDao stockDao;
    private final HttpServletRequest httpServletRequest;
    private final SearchFilter<Product> searchFilter;
    private final CSVService<Product> csvService;
    private final String CSV_FILENAME = "product";
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
        try {

            BaseEntityResponseDto<Product> productResult = productDao.findByProductName(productRequest.getProductName());
            if(productResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("010");
                throw new AppException("010",msg);
            }

            Product product = new Product();
            product.setId(productDao.getProductId());
            product.setProductName(productRequest.getProductName());
            product.setProductDesc(productRequest.getProductDesc());
            product.setPrice(productRequest.getPrice());
            product.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            product.setExpiryDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(productRequest.getExpiryDate()), LocalTime.MIDNIGHT)));

            productDao.saveEntity(product);
            response.setStatus(SUCCESS);
            response.setCode("008");
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
    
            stockDao.deleteStockByProudctID(id);
            purchaseDao.deleteByProductID(id);
            productDao.deleteByProductID(id);
            String msg = AppTools.appGetMessage("007");
            response.setStatus(SUCCESS);
            response.setCode("007");
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
    public Object updateProductById(Long id, ProductUpdateRequest productUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE PRODUCT BY ID");
        SuccessResponse<Product> response = new SuccessResponse<>();
        try {

            //check product id
            BaseEntityResponseDto<Product> productResult = productDao.findByProductID(id);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
                String msg = AppTools.appGetMessage("006");
                throw new AppException("006",msg);
            }

            //check target product name
            if(productUpdateRequest.getProductName()!=null && !productUpdateRequest.getProductName().isEmpty() && !productUpdateRequest.getProductName().isBlank()){
                BaseEntityResponseDto<Product> targetProductResult = productDao.findByProductName(productUpdateRequest.getProductName());
                if(targetProductResult.getEntity()!=null){
                    String msg = AppTools.appGetMessage("010");
                    throw new AppException("010",msg);
                }
            }

            //updated
            Product existingProduct = productResult.getEntity();
            Optional.ofNullable(productUpdateRequest.getProductName()).ifPresent(existingProduct::setProductName);
            Optional.ofNullable(productUpdateRequest.getProductDesc()).ifPresent(existingProduct::setProductDesc);
            Optional.ofNullable(productUpdateRequest.getPrice()).ifPresent(existingProduct::setPrice);
            Optional.ofNullable(productUpdateRequest.getExpiryDate())
            .map(date -> Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)))
            .ifPresent(existingProduct::setExpiryDate);
            existingProduct.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            productDao.saveEntity(existingProduct);

            response.setStatus(SUCCESS);
            response.setCode("009");
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

    @Override
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT PRODUCT");
        try {
            
            Specification<Product> products = new CustomSpecification<>(q);
            BaseEntityResponseDto<Product> productResult = productDao.search(products);
            csvService.export(productResult.getEntityList(),Product.class, CSV_FILENAME+AppTools.getCurrentDateWithFormatString("YYYY-MM-dd-HH-mm-ss")+".csv");

        }catch (DatabaseException e) {
            throw e;   
        }catch (CsvException e) {
            throw new AppException("011",e.getMessage(),true); 
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT PRODUCT");
        try {

            List<Product> productList = csvService.parseCsv(file, Product.class);
            productDao.saveEntities(productList);
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("014"));
            response.setCode("014");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (CsvException e) {
            throw new AppException("013",e.getMessage(),true); 
        }catch (AppException e) {
            throw e;   
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),true);
        
    }
    
    }

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH PRODUCT");
        SuccessResponse<Page<Product>> response = new SuccessResponse<>();
        try {

            Specification<Product> products = new CustomSpecification<>(q);
            PageRequestDto pageRequestDto = new PageRequestDto();
            pageRequestDto.setPageNo(pageNo);
            pageRequestDto.setPageSize(pageSize);
            pageRequestDto.setSort(sort);
            pageRequestDto.setSortByColumn(sortByColum);
            Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);
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

    
}

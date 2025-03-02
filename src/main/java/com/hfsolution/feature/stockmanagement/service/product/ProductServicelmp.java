package com.hfsolution.feature.stockmanagement.service.product;

import static com.hfsolution.app.constant.AppResponseCode.FAIL_CODE;
import static com.hfsolution.app.constant.AppResponseCode.SUCCESS_CODE;
import static com.hfsolution.app.constant.AppResponseStatus.SUCCESS;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;

import com.hfsolution.app.dto.SuccessResponse;
import com.hfsolution.app.exception.AppException;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.properties.CloudinaryProperties;
import com.hfsolution.app.services.CustomSpecification;

import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.CSVHelper;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.dao.CustomerDao;
import com.hfsolution.feature.stockmanagement.dao.ProductDao;
import com.hfsolution.feature.stockmanagement.dto.CsvRepresentation.ProductCsv;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductRequest;
import com.hfsolution.feature.stockmanagement.dto.request.product.ProductUpdateRequest;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.util.product.ExcelUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import static com.hfsolution.app.constant.AppConstant.*;

@Service
@RequiredArgsConstructor
public class ProductServicelmp implements ProductService {
    
    private final CloudinaryProperties cloudinaryProperties;
    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final String CSV_FILENAME = "product";

   

    @Override
    public Object addProduct(ProductRequest productRequest) {

        httpServletRequest.setAttribute(ACTION,"ADD PRODUCT");
        SuccessResponse<Product> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            BaseEntityResponseDto<Product> productResult = productDao.findByProductName(productRequest.getProductName());
            if(productResult.getEntity()!=null && !productResult.getEntity().isDeleted()){
                String msg = AppTools.appGetMessage("010");
                throw new AppException("010",msg);
            }

            Product product = new Product();
            product.setId(productDao.getProductId());
            product.setProductId(productRequest.getProductId());
            product.setProductName(productRequest.getProductName());
            product.setProductDesc(productRequest.getProductDesc());
            product.setPackSize(productRequest.getPackSize());
            product.setPrice(productRequest.getPrice());
            // product.setImportPrice(productRequest.getImportPrice());
            //product.setFactory(productRequest.getFactory());
            product.setImageUrl(cloudinaryProperties.getDefaultImage());
            product.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            //product.setExpiryDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(productRequest.getExpiryDate()), LocalTime.MIDNIGHT)));

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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    public Object addProduct(ProductRequest productRequest,MultipartFile file) {

        httpServletRequest.setAttribute(ACTION,"ADD PRODUCT");
        SuccessResponse<Product> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            
            BaseEntityResponseDto<Product> productResult = productDao.findByProductName(productRequest.getProductName());
            Product product = new Product();
            if(productResult.getEntity()!=null){
                String msg = AppTools.appGetMessage("010");
                throw new AppException("010",msg);
            }
            if (file == null){
                product.setImageUrl(cloudinaryProperties.getDefaultImage());
            }else{
                product.setImageUrl((String)uploadImage(file, STOCK_PRODUCT+"/"+productRequest.getProductName()).get("secure_url"));
            }
            product.setId(productDao.getProductId());
            product.setProductId(productRequest.getProductId());
            product.setProductName(productRequest.getProductName());
            product.setProductDesc(productRequest.getProductDesc());
            product.setPackSize(productRequest.getPackSize());
            product.setPrice(productRequest.getPrice());
            // product.setImportPrice(productRequest.getImportPrice());
            //product.setFactory(productRequest.getFactory());
            product.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            //product.setExpiryDate(Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(productRequest.getExpiryDate()), LocalTime.MIDNIGHT)));
            
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    public Object deleteProductById(Long id) {

        httpServletRequest.setAttribute(ACTION,"DELETE PRODUCT BY ID");
        SuccessResponse<Product> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            Product product =  productDao.deleteByProductID(id).getEntity();
            // ProductHistory productHistory = new ProductHistory();
            // BeanUtils.copyProperties(product, productHistory);
            // productHistory.setDeletedDate(new Timestamp(System.currentTimeMillis()));
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }

    @Override
    public Object updateProductById(Long id, ProductUpdateRequest productUpdateRequest) {

        httpServletRequest.setAttribute(ACTION,"UPDATE PRODUCT BY ID");
        SuccessResponse<Product> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            //check product id
            BaseEntityResponseDto<Product> productResult = productDao.findByProductID(id);
            if(!productResult.getStatus().equals(SUCCESS) || productResult.getEntity()==null){
                String msg = AppTools.appGetMessage("006");
                throw new AppException("006",msg);
            }

            // //check target product name
            if(productUpdateRequest.getProductName()!=null && !productUpdateRequest.getProductName().isEmpty() && !productUpdateRequest.getProductName().isBlank()){
                BaseEntityResponseDto<Product> targetProductResult = productDao.findByProductName(productUpdateRequest.getProductName());
                if(targetProductResult.getEntity()!=null && targetProductResult.getEntity().getId() != id){
                    String msg = AppTools.appGetMessage("010");
                    throw new AppException("010",msg);
                }
            }

            //updated
            Product existingProduct = productResult.getEntity();
            Optional.ofNullable(productUpdateRequest.getProductName()).ifPresent(existingProduct::setProductName);
            Optional.ofNullable(productUpdateRequest.getProductId()).ifPresent(existingProduct::setProductId);
            Optional.ofNullable(productUpdateRequest.getProductDesc()).ifPresent(existingProduct::setProductDesc);
            Optional.ofNullable(productUpdateRequest.getPackSize()).ifPresent(existingProduct::setPackSize);
            Optional.ofNullable(productUpdateRequest.getPrice()).ifPresent(existingProduct::setPrice);
            // Optional.ofNullable(productUpdateRequest.getImportPrice()).ifPresent(existingProduct::setImportPrice);
            //Optional.ofNullable(productUpdateRequest.getFactory()).ifPresent(existingProduct::setFactory);
            // Optional.ofNullable(productUpdateRequest.getExpiryDate())
            // .map(date -> Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)))
            // .ifPresent(existingProduct::setExpiryDate);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }

    @Override
    public Object updateProductById(Long id, ProductUpdateRequest productUpdateRequest, MultipartFile file) {

        httpServletRequest.setAttribute(ACTION,"UPDATE PRODUCT BY ID");
        SuccessResponse<Product> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
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
            // String imageUrl = (String)uploadImage(file,STOCK_PRODUCT+"/"+String.valueOf(id)).get("secure_url");
            Product existingProduct = productResult.getEntity();
            if(file == null){
                existingProduct.setImageUrl(cloudinaryProperties.getDefaultImage());
            }else{

                existingProduct.setImageUrl((String)uploadImage(file,STOCK_PRODUCT+"/"+String.valueOf(id)).get("secure_url"));
            }
            Optional.ofNullable(productUpdateRequest.getProductName()).ifPresent(existingProduct::setProductName);
            Optional.ofNullable(productUpdateRequest.getProductDesc()).ifPresent(existingProduct::setProductDesc);
            Optional.ofNullable(productUpdateRequest.getPrice()).ifPresent(existingProduct::setPrice);
            // Optional.ofNullable(productUpdateRequest.getImportPrice()).ifPresent(existingProduct::setImportPrice);
            //Optional.ofNullable(productUpdateRequest.getFactory()).ifPresent(existingProduct::setFactory);
            // Optional.ofNullable(productUpdateRequest.getExpiryDate())
            // .map(date -> Timestamp.valueOf(LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)))
            // .ifPresent(existingProduct::setExpiryDate);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }

    }

    @Override
    public void export(String q) {
        httpServletRequest.setAttribute(ACTION, "EXPORT PRODUCT");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {
            Specification<Product> products = new CustomSpecification<>(q);
            BaseEntityResponseDto<Product> productResult = productDao.search(products);
            httpServletResponse.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=Product.xlsx";
            httpServletResponse.setHeader(headerKey, headerValue);
            ExcelUtil product = new ExcelUtil(productResult.getEntityList());
            product.exportDataToExcel(httpServletResponse);




        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("011",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true); 
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
    }

    @Override
    public Object importData(MultipartFile file) {
        httpServletRequest.setAttribute(ACTION, "IMPORT PRODUCT");
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            if(ExcelUtil.isValidExcelFile(file)){
                try {
                    List<Product> products = ExcelUtil.getProductsDataFromExcel(file.getInputStream());
                    productDao.saveEntities(products);
                } catch (IOException e) {
                    throw new IllegalArgumentException("The file is not a valid excel file");
                }
            }
            SuccessResponse<?> response = new SuccessResponse<>();
            response.setStatus(SUCCESS);
            response.setMsg(AppTools.appGetMessage("014"));
            response.setCode("014");
            return response;
        }catch (DatabaseException e) {
            throw e;   
        }catch (AppException e) {
            throw new AppException("013",e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }catch(Exception e){
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        
    }
    
    }

    @Override
    public Object search(String q, int pageNo, int pageSize, Direction sort, String sortByColum) {

        httpServletRequest.setAttribute(ACTION,"SEARCH PRODUCT");
        SuccessResponse<Page<Product>> response = new SuccessResponse<>();
        String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
        long startTime = System.currentTimeMillis();
        try {

            
            BaseEntityResponseDto<Product> productResult = productDao.search(q, pageNo, pageSize, sort, sortByColum);
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
            throw new AppException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime),true);
        }
       
    }

    private Map uploadImage(MultipartFile file, String imageName) throws IOException{
       
        Cloudinary cloudinary = new Cloudinary(cloudinaryProperties.getUrl());
        System.out.println(cloudinary.config.cloudName);
        // Upload the image
        Map params1 = ObjectUtils.asMap(
            "use_filename", true,
            "unique_filename", false,
            "overwrite", true,
            "quality", "auto",
            "public_id", imageName
        );
        
        return cloudinary.uploader().upload(file.getBytes(), params1);
    }

    
}

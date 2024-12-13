package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hfsolution.app.constant.AppResponseCode.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hfsolution.app.config.database.context.IDataSourceContextHolder;
import com.hfsolution.app.dao.BaseDBDao;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.dto.PageRequestDto;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.services.CustomSpecification;
import com.hfsolution.app.util.AppTools;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.app.util.JsonUtil;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.repository.ProductRepository;


@Service
public class ProductDao extends BaseDBDao<Product, Long>{
  
  private ProductRepository productRepository;
  @Autowired
  private ProductHistoryDao productHistoryDao;

  public ProductDao(ProductRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.productRepository = repository;
  }

  public Long getProductId(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      
      return productRepository.getNextProductId();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Product> findByProductID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {
     
      Optional<Product> productOpt = productRepository.findById(id);
      Product product = new Product();
      if(productOpt.isPresent()){
        product = productOpt.get();
      }else{
        BeanUtils.copyProperties(productHistoryDao.findByProductHistoryID(id).getEntity(), product);
      }
    
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(product);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Product> findByProductName(String name){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Product product = productRepository.findByProductName(name);

      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(product);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

 

  @SuppressWarnings("unchecked")
  public BaseEntityResponseDto<Product> search(String q, int pageNo, int pageSize, Direction sort, String sortByColum){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {
      Specification<Product> products = new CustomSpecification<>(q);
      PageRequestDto pageRequestDto = new PageRequestDto();
      pageRequestDto.setPageNo(pageNo);
      pageRequestDto.setPageSize(pageSize);
      pageRequestDto.setSort(sort);
      pageRequestDto.setSortByColumn(sortByColum);
      Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

      Page<Product> entity = productRepository.findAll(products,pageable);
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setPage(entity);
      
      appModel.setStatus(SUCCESS);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Product> search(Specification<Product> products){
    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {

      List<Product> entity = productRepository.findAll(products);
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  @Modifying
  @Transactional
  public BaseEntityResponseDto<Product> deleteByProductID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Product product = productRepository.findById(id).get();
      productRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setEntity(product);
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }
  
  
}

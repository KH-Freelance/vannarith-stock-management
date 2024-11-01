package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;
import static com.hfsolution.app.constant.AppResponseCode.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hfsolution.app.config.database.context.IDataSourceContextHolder;
import com.hfsolution.app.dao.BaseDBDao;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.entity.Product;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.repository.ProductRepository;



@Service
public class ProductDao extends BaseDBDao<Product, Long>{


  private ProductRepository productRepository;

  public ProductDao(ProductRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.productRepository = repository;
  }


  public BaseEntityResponseDto<Product> findByProductID(Long id){
    
    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Product entity = productRepository.findById(id).get();
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(entity);
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

      Product entity = productRepository.findByProductName(name);
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Product> search(Specification<Product> stocks, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<Product> entity = productRepository.findAll(stocks,pageable);
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
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

      productRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<Product>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }
  
  
}

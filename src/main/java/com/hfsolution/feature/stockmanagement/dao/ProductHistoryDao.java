package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.hfsolution.app.constant.AppResponseCode.*;

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
import com.hfsolution.feature.stockmanagement.entity.ProductHistory;
import com.hfsolution.feature.stockmanagement.repository.ProductHistoryRepository;


@Service
public class ProductHistoryDao extends BaseDBDao<ProductHistory, Long>{
  
  private ProductHistoryRepository productHistoryRepository;

  public ProductHistoryDao(ProductHistoryRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.productHistoryRepository = repository;
  }

  public Long getProductHistoryId(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      
      return productHistoryRepository.getNextProductHistoryId();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<ProductHistory> findByProductHistoryID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {
     
      ProductHistory productHistory = productHistoryRepository.findById(id).get();

    
      var appModel = new BaseEntityResponseDto<ProductHistory>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(productHistory);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<ProductHistory> findByProductHistoryName(String name){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      ProductHistory productHistory = productHistoryRepository.findByProductName(name);

      var appModel = new BaseEntityResponseDto<ProductHistory>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(productHistory);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

 

  @SuppressWarnings("unchecked")
  public BaseEntityResponseDto<ProductHistory> search(String q, int pageNo, int pageSize, Direction sort, String sortByColum){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {
      Specification<ProductHistory> productHistorys = new CustomSpecification<>(q);
      PageRequestDto pageRequestDto = new PageRequestDto();
      pageRequestDto.setPageNo(pageNo);
      pageRequestDto.setPageSize(pageSize);
      pageRequestDto.setSort(sort);
      pageRequestDto.setSortByColumn(sortByColum);
      Pageable pageable = new PageRequestDto().getPageable(pageRequestDto);

      Page<ProductHistory> entity = productHistoryRepository.findAll(productHistorys,pageable);
      var appModel = new BaseEntityResponseDto<ProductHistory>();
      appModel.setPage(entity);
      
      appModel.setStatus(SUCCESS);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<ProductHistory> search(Specification<ProductHistory> productHistorys){
    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();
    try {

      List<ProductHistory> entity = productHistoryRepository.findAll(productHistorys);
      var appModel = new BaseEntityResponseDto<ProductHistory>();
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
  public BaseEntityResponseDto<ProductHistory> deleteByProductHistoryID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      productHistoryRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<ProductHistory>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }
  
  
}

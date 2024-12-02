package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

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
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseSummaryDTO;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Return;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;
import com.hfsolution.feature.stockmanagement.repository.PurchaseRepository;
import com.hfsolution.feature.stockmanagement.repository.ReturnRepository;


@Service
public class ReturnDao extends BaseDBDao<Return,Long>{


  private ReturnRepository returnRepository;

  public ReturnDao(ReturnRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.returnRepository = repository;
  }

  public BaseEntityResponseDto<Return> searchReturn(Specification<Return> returns, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<Return> entity = returnRepository.findAll(returns,pageable);
      var appModel = new BaseEntityResponseDto<Return>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  public Long getReturnId(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      
      return returnRepository.getNextReturnId();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }



  // public BaseEntityResponseDto<Purchase> findPurchaseByProductID(Long id){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     Purchase entity = purchaseRepository.findByProductId(id);
  //     var appModel = new BaseEntityResponseDto<Purchase>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntity(entity);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  // @Modifying
  // @Transactional
  // public BaseEntityResponseDto<Purchase> deleteByProductID(Long id){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     purchaseRepository.deleteByProductId(id);
  //     var appModel = new BaseEntityResponseDto<Purchase>();
  //     appModel.setStatus(SUCCESS);
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }




  
  
}

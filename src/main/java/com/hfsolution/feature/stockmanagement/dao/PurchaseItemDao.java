package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.sql.Timestamp;
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
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.repository.PurchaseItemRepository;


@Service
public class PurchaseItemDao extends BaseDBDao<PurchaseItem,Long>{


  private PurchaseItemRepository purchaseItemRepository;

  public PurchaseItemDao(PurchaseItemRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.purchaseItemRepository = repository;
  }

  public Long getPurchaseItemCode(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      
      return purchaseItemRepository.getNextPurchaseItemCode();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public Long getPurchaseItemId(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      
      return purchaseItemRepository.getNextPurchaseItemId();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  // public BaseEntityResponseDto<PurchaseItem> findPurchaseItemByProductID(Long id){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     PurchaseItem entity = PurchaseItemRepository.findByProductId(id);
  //     var appModel = new BaseEntityResponseDto<PurchaseItem>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntity(entity);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  public BaseEntityResponseDto<PurchaseItem> search(Specification<PurchaseItem> PurchaseItem, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<PurchaseItem> entity = purchaseItemRepository.findAll(PurchaseItem,pageable);
      var appModel = new BaseEntityResponseDto<PurchaseItem>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  // public BaseEntityResponseDto<PurchaseItem> findAllByStatusAndCreatedDateBetween(String status, String startDate, String endDate){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     List<PurchaseItem> entities = purchaseItemRepository.findAllByStatusAndCreatedDateBetween(status, Timestamp.valueOf(startDate),Timestamp.valueOf(endDate));
  //     var appModel = new BaseEntityResponseDto<PurchaseItem>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntityList(entities);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  public BaseEntityResponseDto<PurchaseItem> search(Specification<PurchaseItem> PurchaseItem){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      List<PurchaseItem> entity = purchaseItemRepository.findAll(PurchaseItem);
      var appModel = new BaseEntityResponseDto<PurchaseItem>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  // public BaseEntityResponseDto<PurchaseItem> findPurchaseItemByProductName(String name){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     PurchaseItem entity = purchaseItemRepository.findByProductName(name);
  //     var appModel = new BaseEntityResponseDto<PurchaseItem>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntity(entity);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  // public BaseEntityResponseDto<PurchaseItem> findPurchaseItemByCustomerName(String name){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     PurchaseItem entity = purchaseItemRepository.findByCustomerName(name);
  //     var appModel = new BaseEntityResponseDto<PurchaseItem>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntity(entity);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  @Modifying
  @Transactional
  public BaseEntityResponseDto<PurchaseItem> deletePurchaseItemByID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      purchaseItemRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<PurchaseItem>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

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

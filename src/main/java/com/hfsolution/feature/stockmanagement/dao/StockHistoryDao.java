package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

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
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;
import com.hfsolution.feature.stockmanagement.repository.StockHistoryRepository;
import com.hfsolution.feature.stockmanagement.repository.StockRepository;



@Service
public class StockHistoryDao extends BaseDBDao<StockHistory,Long>{


  private StockHistoryRepository stockHistoryRepository;

  public StockHistoryDao(StockHistoryRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.stockHistoryRepository = repository;
  }

  public Long getStockHistoryId(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      return stockHistoryRepository.getNextStockHistoryId();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<StockHistory> findAllByStockId(long stockId, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<StockHistory> entity = stockHistoryRepository.findAllByStockId(stockId,pageable);
      var appModel = new BaseEntityResponseDto<StockHistory>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  public BaseEntityResponseDto<StockHistory> search(Specification<StockHistory> stockHistories, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<StockHistory> entity = stockHistoryRepository.findAll(stockHistories,pageable);
      var appModel = new BaseEntityResponseDto<StockHistory>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  // public BaseEntityResponseDto<Stock> findStockByProductID(Long id){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     Stock entity = stockRepository.findByProductId(id);
  //     var appModel = new BaseEntityResponseDto<Stock>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntity(entity);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  public BaseEntityResponseDto<StockHistory> searchStock(Specification<StockHistory> stocks, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<StockHistory> entity = stockHistoryRepository.findAll(stocks,pageable);
      var appModel = new BaseEntityResponseDto<StockHistory>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  // public BaseEntityResponseDto<Stock> searchStock(Specification<Stock> stocks){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     List<Stock> entity = stockRepository.findAll(stocks);
  //     var appModel = new BaseEntityResponseDto<Stock>();
  //     appModel.setStatus(SUCCESS);
  //     appModel.setEntityList(entity);
  //     appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  // public BaseEntityResponseDto<Stock> findStockByProductName(String name){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     Stock entity = stockRepository.findByProductName(name);
  //     var appModel = new BaseEntityResponseDto<Stock>();
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
  // public BaseEntityResponseDto<Stock> deleteStockHistoryByID(Long id){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     stockHistoryRepository.deleteById(id);
  //     var appModel = new BaseEntityResponseDto<Stock>();
  //     appModel.setStatus(SUCCESS);
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  // @Modifying
  // @Transactional
  // public BaseEntityResponseDto<Stock> deleteStockByProudctID(Long id){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {

  //     stockRepository.deleteByProductId(id);
  //     var appModel = new BaseEntityResponseDto<Stock>();
  //     appModel.setStatus(SUCCESS);
  //     return appModel;

  //   } catch (Exception e) {
  //     throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }

  // }

  // public BaseEntityResponseDto<Stock> findStocksByProductIds(List<Long> productIds) {
  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {
  //       List<Stock> stocks = stockRepository.findByProductIds(productIds);
  //       var appModel = new BaseEntityResponseDto<Stock>();
  //       appModel.setStatus(SUCCESS);
  //       appModel.setEntityList(stocks);
  //       appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
  //       return appModel;

  //   } catch (Exception e) {
  //       throw new DatabaseException(FAIL_CODE, e.getMessage(), InfoGenerator.generateInfo(currentMethodName, startTime));
  //   }
  // }

}

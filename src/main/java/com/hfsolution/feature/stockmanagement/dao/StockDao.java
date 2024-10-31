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
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.repository.StockRepository;



@Service
public class StockDao extends BaseDBDao<Stock,Long>{


  private StockRepository stockRepository;

  public StockDao(StockRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.stockRepository = repository;
  }

  public BaseEntityResponseDto<Stock> findStockByProductID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Stock entity = stockRepository.findByProductId(id);
      var appModel = new BaseEntityResponseDto<Stock>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Stock> searchStock(Specification<Stock> stocks, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<Stock> entity = stockRepository.findAll(stocks,pageable);
      var appModel = new BaseEntityResponseDto<Stock>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Stock> findStockByProductName(String name){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Stock entity = stockRepository.findByProductName(name);
      var appModel = new BaseEntityResponseDto<Stock>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  @Modifying
  @Transactional
  public BaseEntityResponseDto<Stock> deleteStockByProductID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      stockRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<Stock>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  @Modifying
  @Transactional
  public BaseEntityResponseDto<Stock> deleteStockByProductName(String name){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      stockRepository.deleteByProductName(name);
      var appModel = new BaseEntityResponseDto<Stock>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  
  
}

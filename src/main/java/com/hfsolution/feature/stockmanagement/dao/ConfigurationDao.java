package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.hfsolution.app.constant.AppResponseCode.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hfsolution.app.config.database.context.IDataSourceContextHolder;
import com.hfsolution.app.dao.BaseDBDao;
import com.hfsolution.app.dto.BaseEntityResponseDto;
import com.hfsolution.app.exception.DatabaseException;
import com.hfsolution.app.util.InfoGenerator;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Configuration;
import com.hfsolution.feature.stockmanagement.repository.CustomerRepository;
import com.hfsolution.feature.stockmanagement.repository.ConfigurationRepository;


@Service
public class ConfigurationDao extends BaseDBDao<Configuration, Long>{

  private ConfigurationRepository scheduleRepository;

  public ConfigurationDao(ConfigurationRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.scheduleRepository = repository;
  }

 

  public BaseEntityResponseDto<Configuration> findActive(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      List<Configuration> entity = scheduleRepository.findAllByActiveTrue();
      var appModel = new BaseEntityResponseDto<Configuration>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Configuration> findFunctionWithActive(List<String> functionType){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      List<Configuration> entityList = scheduleRepository.findByFunctionTypeInAndActiveTrue(functionType);
      var appModel = new BaseEntityResponseDto<Configuration>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entityList);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  @Async("jpaExecutor")
  public CompletableFuture<BaseEntityResponseDto<Configuration>> findFunctionTypeAndActiveTrue(String type) {
      String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
      long startTime = System.currentTimeMillis();

      try {
          Configuration entity = scheduleRepository.findByFunctionTypeAndActiveTrue(type);
          var appModel = new BaseEntityResponseDto<Configuration>();
          appModel.setStatus(SUCCESS);
          appModel.setEntity(entity);
          appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));

          return CompletableFuture.completedFuture(appModel);

      } catch (Exception e) {
          throw new DatabaseException(
              FAIL_CODE,
              e.getMessage(),
              InfoGenerator.generateInfo(currentMethodName, startTime)
          );
      }
  }


  public BaseEntityResponseDto<Configuration> search(Specification<Configuration> schedules, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Page<Configuration> entity = scheduleRepository.findAll(schedules,pageable);
      var appModel = new BaseEntityResponseDto<Configuration>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  public BaseEntityResponseDto<Configuration> search(Specification<Configuration> schedules){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      List<Configuration> entity = scheduleRepository.findAll(schedules);
      var appModel = new BaseEntityResponseDto<Configuration>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }



  
}

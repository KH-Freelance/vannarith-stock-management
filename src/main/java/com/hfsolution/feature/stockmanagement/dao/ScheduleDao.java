package com.hfsolution.feature.stockmanagement.dao;


import static com.hfsolution.app.constant.AppResponseStatus.*;

import java.sql.Timestamp;
import java.util.List;

import static com.hfsolution.app.constant.AppResponseCode.*;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Schedule;
import com.hfsolution.feature.stockmanagement.repository.CustomerRepository;
import com.hfsolution.feature.stockmanagement.repository.ScheduleRepository;


@Service
public class ScheduleDao extends BaseDBDao<Schedule, Long>{

  private ScheduleRepository scheduleRepository;

  public ScheduleDao(ScheduleRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.scheduleRepository = repository;
  }

 

  public BaseEntityResponseDto<Schedule> findActiveAndNotYetExecute(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      List<Schedule> entity = scheduleRepository.findAllByActiveTrueAndExecuteFalse();
      var appModel = new BaseEntityResponseDto<Schedule>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Schedule> search(Specification<Schedule> schedules, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Page<Schedule> entity = scheduleRepository.findAll(schedules,pageable);
      var appModel = new BaseEntityResponseDto<Schedule>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }


  public BaseEntityResponseDto<Schedule> search(Specification<Schedule> schedules){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      List<Schedule> entity = scheduleRepository.findAll(schedules);
      var appModel = new BaseEntityResponseDto<Schedule>();
      appModel.setStatus(SUCCESS);
      appModel.setEntityList(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }



  
}

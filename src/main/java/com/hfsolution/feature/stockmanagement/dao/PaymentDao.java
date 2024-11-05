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
import com.hfsolution.feature.stockmanagement.entity.Payment;
import com.hfsolution.feature.stockmanagement.repository.PaymentRepository;



@Service
public class PaymentDao extends BaseDBDao<Payment, Long>{


  private PaymentRepository PaymentRepository;

  public PaymentDao(PaymentRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.PaymentRepository = repository;
  }

  public Long getPaymentId(){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      
      return PaymentRepository.getNextPaymentId();

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Payment> findByPaymentID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Payment entity = PaymentRepository.findById(id).get();
      var appModel = new BaseEntityResponseDto<Payment>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Payment> search(Specification<Payment> Payments, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<Payment> entity = PaymentRepository.findAll(Payments,pageable);
      var appModel = new BaseEntityResponseDto<Payment>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  // public BaseEntityResponseDto<Payment> findByPaymentName(String name){

  //   String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
  //   long startTime = System.currentTimeMillis();

  //   try {
  //     Payment entity = PaymentRepository.findByPaymentName(name);
  //     var appModel = new BaseEntityResponseDto<Payment>();
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
  public BaseEntityResponseDto<Payment> deleteByPaymentID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      PaymentRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<Payment>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }  

  @Modifying
  @Transactional
  public BaseEntityResponseDto<Payment> deleteByPurchaseId(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      PaymentRepository.deleteByPurchaseId(id);
      var appModel = new BaseEntityResponseDto<Payment>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }  
  
}

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
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Stock;
import com.hfsolution.feature.stockmanagement.repository.CustomerRepository;
import com.hfsolution.feature.stockmanagement.repository.ProductRepository;



@Service
public class CustomerDao extends BaseDBDao<Customer, Long>{


  private CustomerRepository customerRepository;

  public CustomerDao(CustomerRepository repository, @Qualifier("postgressDataSourceContextHolder") IDataSourceContextHolder dataSourceDCContextHolder) {
    super(repository, dataSourceDCContextHolder);
    this.customerRepository = repository;
  }


  public BaseEntityResponseDto<Customer> findByCustomerID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Customer entity = customerRepository.findById(id).get();
      var appModel = new BaseEntityResponseDto<Customer>();
      appModel.setStatus(SUCCESS);
      appModel.setEntity(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Customer> search(Specification<Customer> customers, Pageable pageable){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      Page<Customer> entity = customerRepository.findAll(customers,pageable);
      var appModel = new BaseEntityResponseDto<Customer>();
      appModel.setStatus(SUCCESS);
      appModel.setPage(entity);
      appModel.setSummaryExecInfo(InfoGenerator.generateInfo(currentMethodName, startTime));
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }

  public BaseEntityResponseDto<Customer> findByCustomerName(String name){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {
      Customer entity = customerRepository.findByCustomerName(name);
      var appModel = new BaseEntityResponseDto<Customer>();
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
  public BaseEntityResponseDto<Customer> deleteByCustomerID(Long id){

    String currentMethodName = new Object() {}.getClass().getEnclosingMethod().getName();
    long startTime = System.currentTimeMillis();

    try {

      customerRepository.deleteById(id);
      var appModel = new BaseEntityResponseDto<Customer>();
      appModel.setStatus(SUCCESS);
      return appModel;

    } catch (Exception e) {
      throw new DatabaseException(FAIL_CODE,e.getMessage(),InfoGenerator.generateInfo(currentMethodName, startTime));
    }

  }  
  
}

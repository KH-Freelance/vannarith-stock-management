package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Customer;

public interface CustomerRepository extends IBaseRepository<Customer,Long>, JpaSpecificationExecutor<Customer>{
    Customer findByCustomerName(String name);
    
    @Query(value = "SELECT nextval('customer_id_seq')", nativeQuery = true)
    Long getNextCustomerId();


} 

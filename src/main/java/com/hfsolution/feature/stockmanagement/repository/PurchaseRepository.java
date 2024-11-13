package com.hfsolution.feature.stockmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Purchase;

public interface PurchaseRepository extends IBaseRepository<Purchase,Long>, JpaSpecificationExecutor<Purchase>{


    Purchase findByProductId(Long id);
    Purchase findByCustomerId(Long id);

    

    @Query("SELECT pu FROM Purchase pu WHERE pu.product.productName = :name")
    Purchase findByProductName(String name);

    @Query("SELECT pu FROM Purchase pu WHERE pu.customer.customerName = :name")
    Purchase findByCustomerName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Purchase pu WHERE pu.product.productName = :name")
    void deleteByProductName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Purchase pu WHERE pu.customer.customerName = :name")
    void deleteByCustomerName(String name);


    @Modifying
    @Transactional
    void deleteByProductId(long id);
    
    @Query(value = "SELECT nextval('purchase_id_seq')", nativeQuery = true)
    Long getNextPurchaseId();
    
} 

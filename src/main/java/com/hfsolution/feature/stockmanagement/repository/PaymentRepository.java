package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Payment;

public interface PaymentRepository extends IBaseRepository<Payment,Long>, JpaSpecificationExecutor<Payment>{
    // Payment findByPaymentName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Payment s WHERE s.purchase.id = :id")
    void deleteByPurchaseId(Long id);

    
    @Query(value = "SELECT nextval('payment_id_seq')", nativeQuery = true)
    Long getNextPaymentId();

} 

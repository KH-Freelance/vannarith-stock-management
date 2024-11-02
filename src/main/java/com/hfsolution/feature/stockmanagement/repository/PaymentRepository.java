package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Payment;

public interface PaymentRepository extends IBaseRepository<Payment,Long>, JpaSpecificationExecutor<Payment>{
    // Payment findByPaymentName(String name);

} 

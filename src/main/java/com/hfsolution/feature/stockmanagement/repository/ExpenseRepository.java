package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Expense;

public interface ExpenseRepository extends IBaseRepository<Expense,Long>, JpaSpecificationExecutor<Expense>{

    
    @Query(value = "SELECT nextval('expense_id_seq')", nativeQuery = true)
    Long getNextExpenseId();



} 

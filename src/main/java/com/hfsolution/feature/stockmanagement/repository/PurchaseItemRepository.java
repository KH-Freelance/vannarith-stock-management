package com.hfsolution.feature.stockmanagement.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;

public interface PurchaseItemRepository extends IBaseRepository<PurchaseItem,Long>, JpaSpecificationExecutor<PurchaseItem>{

    
    // List<PurchaseItem>  findAllByStatusAndCreatedDateBetween(String status, Timestamp startDate,Timestamp endDate);

    @Query(value = "SELECT nextval('purchase_id_seq')", nativeQuery = true)
    Long getNextPurchaseItemId();

    @Query(value = "SELECT nextval('purchase_code_seq')", nativeQuery = true)
    Long getNextPurchaseItemCode();

    
} 

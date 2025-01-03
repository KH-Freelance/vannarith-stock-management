package com.hfsolution.feature.stockmanagement.repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseSummaryDTO;
import com.hfsolution.feature.stockmanagement.entity.Purchase;
import com.hfsolution.feature.stockmanagement.enums.PaymentStatus;

public interface PurchaseRepository extends IBaseRepository<Purchase,Long>, JpaSpecificationExecutor<Purchase>{


    // Purchase findByProductId(Long id);
    Purchase findByCustomerId(Long id);

    

    // @Query("SELECT pu FROM Purchase pu WHERE pu.product.productName = :name")
    // Purchase findByProductName(String name);

    @Query("SELECT pu FROM Purchase pu WHERE pu.customer.customerName = :name")
    Purchase findByCustomerName(String name);


    List<Purchase> findAllByCustomerIdAndPaymentStatus(long customerId, PaymentStatus paymentStatus);

    @Query("""
        SELECT new com.hfsolution.feature.stockmanagement.dto.request.purchase.PurchaseSummaryDTO(
            FUNCTION('TO_CHAR', p.createdDate, 'FMMon YY'),
            SUM(p.total),
            p.customer.id,
            c.customerName
        )
        FROM Purchase p
        JOIN Customer c ON p.customer.id = c.id
        WHERE p.createdDate >= :startDate AND p.createdDate <= :endDate
        GROUP BY FUNCTION('TO_CHAR', p.createdDate, 'FMMon YY'), p.customer.id, c.customerName
        ORDER BY FUNCTION('TO_CHAR', p.createdDate, 'FMMon YY'), p.customer.id
    """)
    List<PurchaseSummaryDTO> findByPurchaseInDateRange(
        @Param("startDate") Timestamp startDate,
        @Param("endDate") Timestamp endDate
    );
    

    List<Purchase> findAllByCustomerId(long customerId);
    List<Purchase> findAllByUserId(long userId);

    List<Purchase> findAllByCustomerIdAndCreatedDateBetween(long customerId, Timestamp startDate,Timestamp endDate);

    List<Purchase> findAllByCustomerCustomerNameContainingAndCreatedDateBetween(String customerName, Timestamp startDate,Timestamp endDate);

    List<Purchase> findAllByPurchaseItemsProductProductNameContainingAndCreatedDateBetween(String productName, Timestamp startDate,Timestamp endDate);

    List<Purchase> findAllByCreatedDateBetween(Timestamp startDate,Timestamp endDate);

    // @Modifying
    // @Transactional
    // @Query("DELETE FROM Purchase pu WHERE pu.product.productName = :name")
    // void deleteByProductName(String name);

    // @Modifying
    // @Transactional
    // @Query("DELETE FROM Purchase pu WHERE pu.customer.customerName = :name")
    // void deleteByCustomerName(String name);


    // @Modifying
    // @Transactional
    // void deleteByProductId(long id);
    
    @Query(value = "SELECT nextval('purchase_id_seq')", nativeQuery = true)
    Long getNextPurchaseId();

    @Query(value = "SELECT nextval('purchase_code_seq')", nativeQuery = true)
    Long getNextPurchaseCode();

    Purchase findByPurchaseCode(String purchaseCode);

    List<Purchase> findByPurchaseCodeIn(List<String> purchaseCodes);

    
} 

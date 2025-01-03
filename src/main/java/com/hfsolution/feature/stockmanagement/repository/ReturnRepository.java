package com.hfsolution.feature.stockmanagement.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.PurchaseItem;
import com.hfsolution.feature.stockmanagement.entity.Return;

public interface ReturnRepository extends IBaseRepository<Return,Long>, JpaSpecificationExecutor<Return>{


    // Purchase findByProductId(Long id);
    // Purchase findByCustomerId(Long id);

    // @Query("SELECT pu FROM Purchase pu WHERE pu.product.productName = :name")
    // Purchase findByProductName(String name);

    // @Query("SELECT pu FROM Purchase pu WHERE pu.customer.customerName = :name")
    // Purchase findByCustomerName(String name);

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

    List<Return>  findAllByCreatedDateBetween(Timestamp startDate,Timestamp endDate);

    // @Query("SELECT u, ud FROM User u JOIN UserDetails ud ON u.id = ud.id")
    // List<Object[]> fetchUserDetails();

    
    @Query(value = "SELECT nextval('return_id_seq')", nativeQuery = true)
    Long getNextReturnId();

    




    
} 

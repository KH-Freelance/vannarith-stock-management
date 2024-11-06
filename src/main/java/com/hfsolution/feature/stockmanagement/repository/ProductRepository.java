package com.hfsolution.feature.stockmanagement.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Customer;
import com.hfsolution.feature.stockmanagement.entity.Product;

public interface ProductRepository extends IBaseRepository<Product,Long>, JpaSpecificationExecutor<Product>{


    Product findByProductName(String name);
    List<Customer> findByCreatedDateBetween(Timestamp d1, Timestamp d2);

    
    @Query(value = "SELECT nextval('product_id_seq')", nativeQuery = true)
    Long getNextProductId();

} 

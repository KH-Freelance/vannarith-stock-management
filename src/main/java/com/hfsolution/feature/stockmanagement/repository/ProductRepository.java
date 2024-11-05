package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Product;

public interface ProductRepository extends IBaseRepository<Product,Long>, JpaSpecificationExecutor<Product>{


    Product findByProductName(String name);

    
    @Query(value = "SELECT nextval('product_id_seq')", nativeQuery = true)
    Long getNextProductId();

} 

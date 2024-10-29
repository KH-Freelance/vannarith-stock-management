package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Stock;

public interface StockRepository extends IBaseRepository<Stock,Long>, JpaSpecificationExecutor<Stock>{

    // @Query("SELECT s FROM stock s INNER JOIN product p ON s.product_id = p.product_id WHERE p.product_name = :name")
    // Stock findByProductName(String name);

    @Query("SELECT s FROM Stock s WHERE s.product.productName = :name")
    Stock findByProductName(String name);

    // @Modifying
    // @Query("DELETE FROM stock s WHERE s.product_id IN (SELECT p.product_id FROM product p WHERE p.product_name = :name)")
    // void deleteByProductName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Stock s WHERE s.product.productName = :name")
    void deleteByProductName(String name);
    
} 

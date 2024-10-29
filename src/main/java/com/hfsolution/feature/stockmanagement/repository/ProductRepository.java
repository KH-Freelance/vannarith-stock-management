package com.hfsolution.feature.stockmanagement.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.Product;

public interface ProductRepository extends IBaseRepository<Product,Long>, JpaSpecificationExecutor<Product>{


    Product findByProductName(String name);
    void deleteByProductName(String name);
    
} 

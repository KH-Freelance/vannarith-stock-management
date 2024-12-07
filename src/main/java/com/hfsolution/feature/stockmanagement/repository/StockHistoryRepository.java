package com.hfsolution.feature.stockmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.entity.StockHistory;

public interface StockHistoryRepository extends IBaseRepository<StockHistory,Long>, JpaSpecificationExecutor<StockHistory>{

    // // @Query("SELECT s FROM stock s INNER JOIN product p ON s.product_id = p.product_id WHERE p.product_name = :name")
    // // Stock findByProductName(String name);

    // @Query("SELECT s FROM Stock s WHERE s.product.productName = :name")
    // Stock findByProductName(String name);

    // // @Query("SELECT s FROM Stock s WHERE s.product.id = :id")
    // // Stock findByProductId(Long id);

    // Stock findByProductId(Long id);

    // @Modifying
    // @Transactional
    // void deleteByProductId(Long id);
    // // @Modifying
    // // @Query("DELETE FROM stock s WHERE s.product_id IN (SELECT p.product_id FROM product p WHERE p.product_name = :name)")
    // // void deleteByProductName(String name);

    // @Modifying
    // @Transactional
    // @Query("DELETE FROM Stock s WHERE s.product.productName = :name")
    // void deleteByProductName(String name);

    Page<StockHistory> findAllByStockId(long stockId, Pageable page);

    List<StockHistory> findAllByStockId(long stockId);

    @Modifying
    @Transactional
    void deleteByStockId(Long id);

    @Query(value = "SELECT nextval('stock_history_id_seq')", nativeQuery = true)
    Long getNextStockHistoryId();


    // @Query("SELECT SUM(s.qty) FROM Stock s")
    // Long getTotal();

    // @Query("SELECT s FROM Stock s WHERE s.product.id IN :productIds")
    // List<Stock> findByProductIds(@Param("productIds") List<Long> productIds);

    
} 

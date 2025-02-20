package com.hfsolution.feature.stockmanagement.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.hfsolution.app.repository.IBaseRepository;
import com.hfsolution.feature.stockmanagement.dto.stock.StockPercentageDto;
import com.hfsolution.feature.stockmanagement.entity.Stock;

public interface StockRepository extends IBaseRepository<Stock,Long>, JpaSpecificationExecutor<Stock>{

    // @Query("SELECT s FROM stock s INNER JOIN product p ON s.product_id = p.product_id WHERE p.product_name = :name")
    // Stock findByProductName(String name);

    // List<Stock> findByStockHistoriesUserId(Long userId);

    @Query("""
        SELECT new com.hfsolution.feature.stockmanagement.dto.stock.StockPercentageDto(
            id,
            qty,
            CASE 
                WHEN total_qty <= 0 THEN 0.00
                ELSE (qty * 100.0 / total_qty)
            END AS percentage_qty
        )

        FROM
            Stock  ,
            (SELECT SUM(qty) AS total_qty FROM Stock) AS total where id in :ids
        """)
    List<StockPercentageDto> findPercentage(List<Long> ids);


    
    List<Stock> findAllByCreatedDateBetween(Timestamp startDate,Timestamp endDate);

    // @Query("SELECT s FROM Stock s WHERE s.product.id = :id")
    // Stock findByProductId(Long id);

    Stock findByProductId(Long id);

    Stock findByProductIdAndBatchId(Long id,String batchId);

    Stock findByBatchId(String batctId);

    @Modifying
    @Transactional
    void deleteByProductId(Long id);
    // @Modifying
    // @Query("DELETE FROM stock s WHERE s.product_id IN (SELECT p.product_id FROM product p WHERE p.product_name = :name)")
    // void deleteByProductName(String name);

    // @Modifying
    // @Transactional
    // @Query("DELETE FROM Stock s WHERE s.product.productName = :name")
    // void deleteByProductName(String name);


    @Query(value = "SELECT nextval('stock_id_seq')", nativeQuery = true)
    Long getNextStockId();




    @Query("SELECT SUM(s.qty) FROM Stock s")
    Long getTotal();

    // @Query("SELECT s FROM Stock s WHERE s.product.id IN :productIds")
    // List<Stock> findByProductIds(@Param("productIds") List<Long> productIds);

    
} 
